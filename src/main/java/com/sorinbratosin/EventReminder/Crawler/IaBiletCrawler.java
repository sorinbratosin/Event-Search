package com.sorinbratosin.EventReminder.Crawler;

import com.sorinbratosin.EventReminder.DAO.Event;
import com.sorinbratosin.EventReminder.Service.EventService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class IaBiletCrawler {

    Logger LOG = LoggerFactory.getLogger(IaBiletCrawler.class);

    private Map<String, Integer> monthToInt = new HashMap<>();
    private Map<String, String> eventsMap = new HashMap<>();

    @Autowired
    private EventService eventService;

    public IaBiletCrawler() {
        monthToInt.put("ian", 1);
        monthToInt.put("feb", 2);
        monthToInt.put("mar", 3);
        monthToInt.put("apr", 4);
        monthToInt.put("mai", 5);
        monthToInt.put("iun", 6);
        monthToInt.put("iul", 7);
        monthToInt.put("aug", 8);
        monthToInt.put("sep", 9);
        monthToInt.put("oct", 10);
        monthToInt.put("nov", 11);
        monthToInt.put("dec", 12);

        eventsMap.put("pop", "concerte-pop");
        eventsMap.put("rock", "concerte-rock");
        eventsMap.put("clasica", "concerte-muzica-clasica");
        eventsMap.put("electronica", "concerte-electro");
        eventsMap.put("metal", "concerte-metal");
        eventsMap.put("hip-hop", "concerte-hip-hop");
        eventsMap.put("pop-rock", "concerte-pop-rock");
        eventsMap.put("jazz", "concerte-jazz");
        eventsMap.put("folk", "concerte-folk");
        eventsMap.put("populara", "concerte-populara");
        eventsMap.put("reggae", "concerte-reggae");
        eventsMap.put("fado", "concerte-fado");
        eventsMap.put("conferinte", "conferinte");
        eventsMap.put("teatru", "teatru");
        eventsMap.put("teatru pentru copii", "teatru-pentru-copii");
        eventsMap.put("stand-up", "stand-up-comedy");
        eventsMap.put("festivaluri", "festivaluri");
    }

    //@Scheduled(fixedRate = 1000000)
    public void run() throws InterruptedException {
        LOG.info("A pornit IaBiletCrawler");
        for (Map.Entry<String, String> entry : eventsMap.entrySet()) {
            extract("https://www.iabilet.ro/bilete-" + entry.getValue());
        }
        //extract("https://www.iabilet.ro/bilete-concerte-rock/");
        LOG.info("S-a terminat IaBiletCrawler");
    }

    public List<Event> extract(String html) throws InterruptedException {
        LOG.info("Incep sa parsez fisierul html: " + html);
        List<Event> events = new ArrayList<>();

        System.setProperty("webdriver.chrome.driver", "D:\\IntelliJ projects\\Event-Reminder\\Event-Reminder\\src\\main\\resources\\res\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("start-maximized");
        WebDriver driver = new ChromeDriver(options);
        driver.get(html);

        List<WebElement> button = driver.findElements(By.cssSelector(".btn-more-container a"));
        int i = button.size();
        for (int x = 0; x < i; x++) {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            driver.findElement(By.cssSelector(".btn-more-container a")).click();
            /*WebDriverWait wait = new WebDriverWait(driver, 5);
             wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn-more-container a")));*/
            Thread.sleep(2000);
            jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            List<WebElement> button2 = driver.findElements(By.cssSelector(".btn-more-container a"));
            if (button2.size() == 1) {
                i++;
            }
        }

        String page = driver.getPageSource();
        driver.quit();

        //Document doc = Jsoup.connect(page).get();
        Document doc = Jsoup.parse(page);
        Elements elements = doc.select(".event-list-item");

        for (Element eventElement : elements) {

            Element elementTitle = eventElement.selectFirst(".title span");
            String dateStartDay = eventElement.select(".date-start .date-day").text();
            String dateStartMonth = eventElement.select(".date-start .date-month").text();
            String dateStartYear = eventElement.select(".date-start .date-year").text().replace("'", "");

            String dateEndDay = eventElement.select(".date-end .date-day").text();
            String dateEndMonth = eventElement.select(".date-end .date-month").text();
            String dateEndYear = eventElement.select(".date-end .date-year").text().replace("'", "");

            String location = eventElement.select(".location .venue span").get(0).text();
            String city = eventElement.select(".location .venue span").get(1).text();
            if (city.toLowerCase().equals("romania")) {
                city = "N/A";
            }
            //get event URL
            Element link = eventElement.select("div.col-xs-2 > a").first();
            String url = "https://www.iabilet.ro" + link.attr("href");
            //LOG.info("The URL " + url);

            String price = eventElement.select(".col-xs-2 .details .price").text();
            if (price.isEmpty()) {
                price = "N/A";
            }

            Event event = new Event();

            int startYear = convertYearIfNotEmpty(dateStartYear);
            int endYear = convertYearIfNotEmpty(dateEndYear);

            LocalDate eventStartDate = LocalDate.of(startYear, monthToInt.get(dateStartMonth), Integer.parseInt(dateStartDay));

            LocalDate eventEndDate = null;

            if (!dateEndMonth.isEmpty()) {
                eventEndDate = LocalDate.of(endYear, monthToInt.get(dateEndMonth), Integer.parseInt(dateEndDay));
            }

            event.setName(elementTitle.text());
            event.setDateStart(eventStartDate);
            event.setDateEnd(eventEndDate);
            event.setLocation(location);
            event.setCity(city);
            event.setUrl(url);
            event.setPrice(price);

            String[] s = html.split("-");
            String genre = "";

            for (String j : eventsMap.keySet()) {
                if (s[s.length - 2].equals("pop")) {
                    genre = "pop rock";
                } else if (s[s.length - 1].equals(j)) {
                    genre = j;
                } else if (s[s.length - 1].equals("electro")) {
                    genre = "electronica";
                } else if (s[s.length - 1].equals("hop")) {
                    genre = "hip-hop";
                } else if (s[s.length - 1].equals("copii")) {
                    genre = "teatru pentru copii";
                } else if (s[s.length - 1].equals("comedy")) {
                    genre = "stand-up comedy";
                }
            }

            //LOG.info("THE HTML BEFORE SPLIT: " + Arrays.toString(s));
            //LOG.info("THE HTML AFTER SPLIT: " + Arrays.toString(s));

            event.setGenre(genre);

            /*List<Keyword> keywordList = new ArrayList<>();
            for(String str : keywords) {
                keywordList.add(str);
            }
            event.setKeyword(keywords);*/

            events.add(event);
        }
        for (Event event : events) {
            Integer existingEventsWithSameName = eventService.countAllByName(event.getName());
            if (existingEventsWithSameName == 0) {
                eventService.save(event);
            }
        }
        LOG.info("Am terminat sa parsez fisierul html: " + html + ". Am extras " + events);

        return events;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    private int convertYearIfNotEmpty(String year) {
        int eventYear = LocalDate.now().getYear();
        if (!year.isEmpty()) {
            eventYear = Integer.parseInt(20 + year);
        }
        return eventYear;
    }
}

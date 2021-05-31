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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IaBiletCrawler {

    Logger LOG = LoggerFactory.getLogger(IaBiletCrawler.class);

    private Map<String, Integer> monthToInt = new HashMap<>();

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
    }

    @Scheduled(fixedRate = 30000)
    public void run() throws IOException {
        LOG.info("A pornit IaBiletCrawler");
        extract("https://www.iabilet.ro/bilete-concerte-rock/");
        LOG.info("S-a terminat IaBiletCrawler");
    }

    public List<Event> extract(String html) throws IOException {
        LOG.info("Incep sa parsez fisierul html: " + html);
        List<Event> events = new ArrayList<>();

        System.setProperty("webdriver.chrome.driver", "D:\\IntelliJ projects\\Event-Reminder\\Event-Reminder\\src\\main\\resources\\res\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("start-maximized");
        options.addArguments("--window-size=1920,1080");
        WebDriver driver = new ChromeDriver(options);
        driver.get(html);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        driver.findElement(By.cssSelector(".btn-more-container a")).click();
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn-more-container a")));
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
            String description = eventElement.select(".main-info div").get(2).text();
            String city = eventElement.select(".location .venue span").get(1).text();
            if(city.toLowerCase().equals("romania")) {
                city = "N/A";
            }
            //get event URL
            Element link = eventElement.select("div.col-xs-3 > a").first();
            String url = "iabilet.ro" + link.attr("href");
            //LOG.info("The URL " + url);

            String price = eventElement.select(".col-xs-2 .details .price").text();
            if(price.isEmpty()) {
                price = "N/A";
            }

            Event event = new Event();

            int startYear = convertYearIfNotEmpty(dateStartYear);
            int endYear = convertYearIfNotEmpty(dateEndYear);

            LocalDate eventStartDate = LocalDate.of(startYear, monthToInt.get(dateStartMonth), Integer.parseInt(dateStartDay));

            LocalDate eventEndDate = null;

            if(!dateEndMonth.isEmpty()) {
                eventEndDate = LocalDate.of(endYear, monthToInt.get(dateEndMonth), Integer.parseInt(dateEndDay));
            }

            event.setName(elementTitle.text());
            event.setDateStart(eventStartDate);
            event.setDateEnd(eventEndDate);
            event.setLocation(location);
            event.setDescription(description);
            event.setCity(city);
            event.setUrl(url);
            event.setPrice(price);

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
        if(!year.isEmpty()) {
            eventYear = Integer.parseInt(20 + year);
        }
        return eventYear;
    }
}

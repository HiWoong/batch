package com.ktds.batch.jobs.crawling.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.RequiredArgsConstructor;

import com.ktds.batch.jobs.crawling.dto.CrawlingDetailRes;
import com.ktds.batch.jobs.crawling.dto.openapi.OpenApiDetailInfoDto;
import com.ktds.batch.jobs.crawling.dto.openapi.OpenApiDetailResDto;
import com.ktds.batch.jobs.crawling.dto.openapi.OpenApiParamDto;
import com.ktds.batch.jobs.crawling.dto.CrawlingResDto;

@Service
@RequiredArgsConstructor
public class CrawlingService {

    @Value("${crawling.open-api.base-url}")
    private String openApiUrl;

    @Value("${crawling.open-api.file-path}")
    private String openApiFilePath;

    /**
     * OPEN API 리스트 페이지 크롤링 후 상세 크롤링 작업
     * @return
     */
    public String getOpenAPIList() {
        List<String> apiUrlList = new ArrayList<>();
        List<CrawlingResDto> crawlingList = new ArrayList<>();

        // 크롬 옵션 세팅
        ChromeOptions options = this.setChromeOptions();

        WebDriver driver = new ChromeDriver(options);       // WebDriver 객체 생성
        driver.get(openApiUrl);

        Optional<WebElement> apiElement = driver.findElements(By.cssSelector("div.result-list"))
            .stream().findFirst();

        // 메인 화면 API 리스트 URL 크롤링
        apiElement.ifPresent(el -> {
            List<WebElement> apiList = el.findElements(By.cssSelector("li dt a"));

            for (WebElement api : apiList) {
                String url = api.getDomProperty("href");
                System.out.println("href : " + url);

                apiUrlList.add(url);
            }
        });

        // API 크롤링
        if (!apiUrlList.isEmpty()) {
            for (String apiUrl : apiUrlList) {
                CrawlingResDto response = this.getSelenium(apiUrl);
                crawlingList.add(response);
            }
        }

        // 파일 저장
        String jsonFileNm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_openAPI.json";

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        File jsonFile = new File(openApiFilePath, jsonFileNm);
        jsonFile.getParentFile().mkdirs();

        try {
            mapper.writeValue(jsonFile, crawlingList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return "success";
    }

    /**
     * OPEN API 단일 URL 크롤링
     * @param url
     * @return
     */
    public CrawlingResDto getSelenium(String url) {
        // 크롬 옵션 세팅
        ChromeOptions options = this.setChromeOptions();

        WebDriver driver = new ChromeDriver(options);       // WebDriver 객체 생성
        driver.get(url);

        // CrawlingResDto 생성
        CrawlingResDto resDto = CrawlingResDto.builder().build();

        // 데이터 타입 세팅
        if (url.contains("openapi")) {
            resDto.setDataType("API");
        } else if (url.contains("fileData")) {
            resDto.setDataType("FILE");
        } else {
            resDto.setDataType("OTHER");
        }

        System.out.println("//////////////////////////////TITLE////////////////////////////////////////");
        List<WebElement> titleElements = driver.findElements(By.cssSelector("p.tit.open-api-title"));
        if (!titleElements.isEmpty()) {
            System.out.println(titleElements.get(0).getText().trim());
            // API 제목 세팅
            resDto.setTitle(titleElements.get(0).getText().trim());
        }

        System.out.println("//////////////////////////////CONT 내용////////////////////////////////////////");
        List<WebElement> contElements = driver.findElements(By.cssSelector("div.cont"));
        if (!contElements.isEmpty()) {
            List<WebElement> spans = contElements.get(0).findElements(By.cssSelector("span"));
            if (!spans.isEmpty()) {
                String contText = spans.get(0).getText().trim();
                if (contText.isEmpty()) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    contText = ((String) js.executeScript("return arguments[0].innerText;", spans.get(0))).trim();
                }
                System.out.println(contText);
                // API 설명 세팅
                resDto.setDesc(contText);
            }
        }

        System.out.println("//////////////////////////////메타 데이터////////////////////////////////////////");
        Optional<WebElement> metaButtonElement = driver.findElements(By.cssSelector("button.h36.dropbtn"))
            .stream().findFirst();

        if (metaButtonElement.isPresent()) {
            ConcurrentHashMap<String, String> metaData = new ConcurrentHashMap<>();

            // xml에서 라이선스 데이터 추출하기 위한 Parser 준비
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            List<WebElement> dropDownContents = driver.findElements(By.xpath("//div[@class='file-meta-table-pc']//div[@class='dropdown-content']//a"));

            for (WebElement dropDownContent : dropDownContents) {
                String metaDataText = dropDownContent.getDomProperty("textContent");
                String metaDataUrl = dropDownContent.getDomProperty("href");

                System.out.println(metaDataText + ": " + metaDataUrl);

                metaData.put(metaDataText, metaDataUrl);

                // DCAT 텍스트 크롤링
                if (metaDataText.contains("DCAT")) {
                    // 현재 창 핸들 저장
                    String mainWindow = driver.getWindowHandle();

                    // 새 창 열기
                    ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", metaDataUrl);

                    // 새 창 핸들 가져오기
                    List<String> windows = new ArrayList<>(driver.getWindowHandles());
                    driver.switchTo().window(windows.get(windows.size() - 1));

                    // XML 추출
                    WebElement preTag = driver.findElement(By.cssSelector("div#folder0"));
                    String xmlContent = preTag.getText().trim();

                    System.out.println("xmlContent: " + xmlContent);
                    metaData.put("xmlContent", xmlContent);

                    // xml에서 라이선스 내용 추출
                    Document doc;
                    String license = null;

                    try {
                        doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));
                    } catch (SAXException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                    // xml <dct:rights> 태그 내용 -> 라이선스 정보
                    NodeList rightsList = doc.getElementsByTagName("dct:rights");
                    if (rightsList.getLength() > 0) {
                        license = rightsList.item(0).getTextContent();
                    }

                    metaData.put("license", license);

                    // 새 창 닫기
                    driver.close();

                    // 원래 창으로 돌아가기
                    driver.switchTo().window(mainWindow);
                }
            }

            // 메타데이터 세팅
            resDto.setMetaData(metaData);
        }

        System.out.println("//////////////////////////////OPEN API 상세정보////////////////////////////////////////");
        // dataset-table 클래스를 가진 테이블만 선택
        List<WebElement> tables = driver.findElements(
            By.xpath("//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//table[contains(@class,'dataset-table')]")
        );

        // OpenAPI 정보 Map 생성
        ConcurrentHashMap<String, String> dataMap = new ConcurrentHashMap<>();

        for (WebElement table : tables) {

            List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

            for (WebElement row : rows) {
                // 각 행에서 th와 td 요소 찾기
                List<WebElement> headers = row.findElements(By.tagName("th"));
                List<WebElement> dataCells = row.findElements(By.tagName("td"));

                // th와 td가 모두 있는 경우에만 처리
                if (!headers.isEmpty() && !dataCells.isEmpty()) {
                    String key = headers.get(0).getText().trim();
                    String value = dataCells.get(0).getText().trim();

                    // 링크가 있는 경우 href/onclick을 분석하여 실제 접근 가능한 URL을 추출
                    if (!dataCells.get(0).findElements(By.tagName("a")).isEmpty()) {
                        WebElement link = dataCells.get(0).findElement(By.tagName("a"));
                        String href = link.getDomAttribute("href");


                        // 상대경로면 절대경로로 변환
                        if (href != null && !href.isEmpty() && !href.matches("(?i)^https?://.*")) {
                            try {
                                URL base = new URL(driver.getCurrentUrl());
                                URL resolved = new URL(base, href);
                                href = resolved.toString();
                            } catch (Exception ignore) { }
                        }

                        if (href != null && !href.isEmpty()) {
                            value += " (링크: " + href + ")";
                        }
                    }

                    System.out.println("Key: " + key + ", value: " + value);

                    // OpenAPI 정보 Map에 쌓기
                    dataMap.put(key, value);
                }
            }
        }
        // CrawlingResDto > openAPIInfo 세팅
        resDto.setDatasetInfo(dataMap);

        // 상세기능 or Swagger 형식 구분
        List<WebElement> openAPIDetail = driver.findElements(By.cssSelector("div.open-api-detail"));
        List<WebElement> apiSwagger = driver.findElements(By.cssSelector("div#api-swagger"));

        // CrawlingDetailResDto 리스트 생성
        List<CrawlingDetailRes> detailResDtoList = new ArrayList<>();

        // 상세기능인 경우
        if (!openAPIDetail.isEmpty() && apiSwagger.isEmpty()) {
            System.out.println("//////////////////////////////상세기능////////////////////////////////////////");

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

                // 상세기능 SelectBox 찾기
                WebElement selectElement = driver.findElement(By.xpath("//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//select[@id='open_api_detail_select']"));
                Select select = new Select(selectElement);
                List<WebElement> optionsList = select.getOptions();

                // 상세기능 목록 하나씩 조회
                for (int i = 0; i < optionsList.size(); i++) {
                    // 상세내용 DTO 생성
                    OpenApiDetailResDto detailResDto = OpenApiDetailResDto.builder().build();
                    OpenApiDetailInfoDto detailInfoDto = OpenApiDetailInfoDto.builder().build();

                    String value = optionsList.get(i).getDomAttribute("value");
                    String optionText = optionsList.get(i).getText().trim();

                    System.out.println("상세기능 목록: " + optionText + ", value: " + value);

                    // API명 세팅
                    detailResDto.setApiNm(optionText);

                    // 상세기능 설명 텍스트
                    WebElement previousElement = driver.findElement(By.xpath("//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//div[@id='open-api-detail-result']//h4[contains(@class, 'tit')]"));
                    String previousText = i == 0 ? "" : previousElement.getText();

                    // Select Box 선택 후 조회
                    select.selectByValue(value);
                    WebElement searchBtn = driver.findElement(By.xpath("//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//button[contains(@class,'button') and @title='조회하기']"));
                    searchBtn.click();

                    // 상세기능 설명 텍스트가 이전과 달라질 때까지 기다림
                    WebElement updatedElement = wait.until((ExpectedCondition<WebElement>) wd -> {
                        WebElement element = wd.findElement(By.xpath(
                            "//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//div[@id='open-api-detail-result']//h4[contains(@class, 'tit')]"
                        ));
                        String text = element.getText();

                        return (!text.isEmpty() && !text.equals(previousText)) ? element : null;
                    });

                    // API 설명
                    System.out.println("Changed Title: " + updatedElement.getText().trim());
                    // API 설명 세팅
                    detailResDto.setApiDesc(updatedElement.getText().trim());

                    // DOM 활성화를 위해 요청변수 제목 텍스트까지 화면 스크롤 내리기
                    WebElement domActive = new WebDriverWait(driver, Duration.ofSeconds(90))
                        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//div[@id='open-api-detail-result']/h4[2]")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", domActive);

                    // 필요시 Actions로 추가 스크롤 (호환성 확보)
                    Actions actions = new Actions(driver);
                    actions.moveToElement(domActive).perform();

                    Thread.sleep(1000);

                    // API 상세(활용승인 절차, 신청가능 트래픽, 요청주소, 서비스 URL)
                    WebElement detailInfo = driver.findElement(By.xpath("//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//div[@id='open-api-detail-result']//ul[@class='dot-list']"));
                    List<WebElement> liElements = detailInfo.findElements(By.tagName("li"));

                    // 상세기능 내용 Map 생성
                    ConcurrentHashMap<String, String> detailInfoMap = new ConcurrentHashMap<>();

                    for (WebElement li : liElements) {
                        // key: <strong> 텍스트
                        String infoKey = li.findElement(By.tagName("strong")).getText().trim();
                        // value: <strong> 제외한 나머지 텍스트
                        String infoValue = li.getText().replace(infoKey, "").trim();

                        // 줄바꿈이나 공백 정리
                        infoValue = infoValue.replaceAll("\\s+/\\s+", " / ").replaceAll("\\n+", " ").trim();

                        // 상세기능 내용 Map에 데이터 쌓기
                        detailInfoMap.put(infoKey, infoValue);

                        System.out.println("infoKey: " + infoKey + ", infoValue: " + infoValue);

                        // 엔드포인트, URI, 포트 작업
                        if (infoKey.contains("서비스URL")) {
                            // URL 파싱
                            URL baseUrl = new URL(infoValue);

                            int port = (baseUrl.getPort() == -1) ? 80 : baseUrl.getPort();

                            // 엔드포인트, 포트 세팅
                            detailResDto.setEdpt(infoValue);
                            detailResDto.setPort(String.valueOf(port));
                        } else if (infoKey.contains("요청주소")) {
                            String uri = Optional.ofNullable(infoValue)
                                .filter(v -> v.lastIndexOf("/") > 0)
                                .map(v -> v.substring(v.lastIndexOf("/")))
                                .orElse(infoValue);

                            // URI 세팅
                            detailResDto.setUri(uri);
                        }
                    }

                    // CrawlingDetailInfo > detailInfo 세팅
                    detailInfoDto.setUseInfo(detailInfoMap);

                    // 요청변수, 출력결과 테이블 찾기
                    List<WebElement> tableDivs = driver.findElements(By.xpath("//div[(contains(@style,'display:block') or contains(@style,'display: block'))]//div[@id='open-api-detail-result']//table"));

                    // 요청 파라미터 세팅
                    detailInfoDto.setRequestParam(parseParamTable(tableDivs.get(0)));

                    // 출력 파라미터 세팅
                    detailInfoDto.setResponseParam(parseParamTable(tableDivs.get(1)));

                    System.out.println("//////////////////////////////샘플코드////////////////////////////////////////");
                    // 샘플코드 Map 생성
                    ConcurrentHashMap<String, String> sampleCodeMap = new ConcurrentHashMap<>();

                    List<WebElement> sampleButtons = driver.findElements(By.cssSelector("#sampleCodeAreaDiv > button.sampleCodeType"));
                    if (!sampleButtons.isEmpty()) {
                        WebDriverWait sampleWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                        Pattern langPattern = Pattern.compile("fn_selectSampleCode\\(['\"]([a-zA-Z0-9_\\-]+)['\"]\\)");
                        for (WebElement button : sampleButtons) {
                            String onclickAttr = button.getDomAttribute("onclick");
                            String languageLabel = null;
                            if (onclickAttr != null) {
                                Matcher langMatcher = langPattern.matcher(onclickAttr);
                                if (langMatcher.find()) {
                                    languageLabel = langMatcher.group(1);
                                }
                            }
                            if (languageLabel == null || languageLabel.isEmpty()) {
                                languageLabel = button.getText().trim();
                            }
                            WebElement codeArea = driver.findElement(By.cssSelector("#sampleCodeArea"));
                            String beforeText = getInnerText(driver, codeArea).trim();

                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

                            try {
                                sampleWait.until(d -> {
                                    WebElement area = d.findElement(By.cssSelector("#sampleCodeArea"));
                                    String current = getInnerText(d, area).trim();
                                    return !current.isEmpty() && !current.equals(beforeText);
                                });
                            } catch (Exception ignored) { }

                            codeArea = driver.findElement(By.cssSelector("#sampleCodeArea"));
                            String codeText = getInnerText(driver, codeArea).trim();
                            System.out.println("[샘플코드 - " + languageLabel + "]");
                            System.out.println(codeText);

                            // 샘플코드 Map에 데이터 쌓기
                            sampleCodeMap.put(languageLabel, codeText);
                        }

                        // CrawlingDetailInfoDto > sampleCode 세팅
                        detailInfoDto.setSampleCode(sampleCodeMap);
                    }

                    // DTO 세팅
                    detailResDto.setDetailInfo(detailInfoDto);
                    detailResDtoList.add(detailResDto);
                }
            } catch (NoSuchElementException e) {
                System.err.println("SELECT 요소를 찾지 못했습니다. :" + e.getMessage());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        } else if (openAPIDetail.isEmpty() && !apiSwagger.isEmpty()) {
            // swagger인 경우
            OpenApiDetailResDto detailResDto = OpenApiDetailResDto.builder().build();
            OpenApiDetailInfoDto detailInfoDto = OpenApiDetailInfoDto.builder().build();

            try {
                WebElement swaggerElement = driver.findElement(By.cssSelector("div#api-swagger"));
                WebElement swaggerInfo = swaggerElement.findElement(By.cssSelector("div.information-container"));

                // API 제목, 버전, 설명
                String apiFullName = swaggerInfo.findElement(By.cssSelector("h2.title")).getText().trim();
                String[] parts = apiFullName.split("\\r?\\n");

                String apiName = parts[0].trim();
                String apiVersion = (parts.length > 1) ? parts[1].trim() : "";

                String apiBaseUrl = swaggerInfo.findElement(By.cssSelector("pre.base-url")).getText().trim();

                String extracted = apiBaseUrl.replace("[", "")
                    .replace("]", "")
                    .replace("Base URL:", "")
                    .trim();

                // http/https 없는 경우 http:// 붙이기
                if (!extracted.matches("(?i)^https?://.*")) {
                    extracted = "http://" + extracted;
                }

                // URL 파싱
                URL baseUrl = new URL(extracted);

                String protocol = baseUrl.getProtocol();
                String domain = baseUrl.getHost();
                int port = (baseUrl.getPort() == -1) ? 80 : baseUrl.getPort();
                String path = baseUrl.getPath();

                // 마지막 / 기준으로 endpoint와 uri 분리
                int lastSlash = path.lastIndexOf("/");
                String endpoint = (lastSlash > 0) ? path.substring(0, lastSlash) : "";
                String uri = (lastSlash > 0) ? path.substring(lastSlash) : path;

                System.out.println("apiName: " + apiName);
                System.out.println("apiVersion: " + apiVersion);
                System.out.println("apiBaseUrl: " + apiBaseUrl);
                System.out.println("port: " + port);
                System.out.println("endpoint: " + protocol + "://" + domain + endpoint);
                System.out.println("uri: " + uri);

                WebElement apiDescription = swaggerInfo.findElement(By.cssSelector("div.markdown"));
                System.out.println("apiDescription: " + apiDescription.getText().trim());

                // API명, API 설명, 엔드포인트, URI, 포트 세팅
                detailResDto.setApiNm(apiName);
                detailResDto.setApiDesc(apiDescription.getText().trim());
                detailResDto.setEdpt(protocol + "://" + domain + endpoint);
                detailResDto.setUri(uri);
                detailResDto.setPort(Integer.toString(port));

            } catch (MalformedURLException e) {
                System.out.println("URL 파싱 실패: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("예상치 못한 오류 발생: " + e.getMessage());
            }

            System.out.println("//////////////////////////////SWAGGER 정보////////////////////////////////////////");
            String swaggerUrl = null;
            String swaggerJson = null;

            Pattern urlPattern = Pattern.compile("var\\s+swaggerUrl\\s*=\\s*['\"]([^'\"]*)['\"];", Pattern.DOTALL);
            Pattern jsonBacktickPattern = Pattern.compile("var\\s+swaggerJson\\s*=\\s*`([\\s\\S]*?)`\\s*;", Pattern.DOTALL);
            Pattern jsonQuotePattern = Pattern.compile("var\\s+swaggerJson\\s*=\\s*['\"]([\\s\\S]*?)['\"]\\s*;", Pattern.DOTALL);

            List<WebElement> scriptTags = driver.findElements(By.tagName("script"));
            for (WebElement script : scriptTags) {
                String scriptText = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent;", script);
                if (scriptText == null || scriptText.isEmpty()) {
                    continue;
                }

                if (swaggerUrl == null) {
                    Matcher m = urlPattern.matcher(scriptText);
                    if (m.find()) {
                        swaggerUrl = m.group(1);
                    }
                }

                if (swaggerJson == null) {
                    Matcher mBack = jsonBacktickPattern.matcher(scriptText);
                    if (mBack.find()) {
                        swaggerJson = mBack.group(1);
                    } else {
                        Matcher mQuote = jsonQuotePattern.matcher(scriptText);
                        if (mQuote.find()) {
                            swaggerJson = mQuote.group(1);
                        }
                    }
                }

                if (swaggerUrl != null && swaggerJson != null) {
                    break;
                }
            }

            // swagger 정보 Map 생성
            ConcurrentHashMap<String, String> swaggerInfo = new ConcurrentHashMap<>();

            if (swaggerUrl != null && !swaggerUrl.isEmpty()) {
                System.out.println("swaggerUrl: " + swaggerUrl);

                // swaggerUrl Map에 쌓기
                swaggerInfo.put("swaggerUrl", swaggerUrl);
            }
            if (swaggerJson != null && !swaggerJson.isEmpty()) {
                System.out.println("swaggerJson: " + swaggerJson);

                // swaggerJson Map에 쌓기
                swaggerInfo.put("swaggerJson", swaggerJson);
            }

            // CrawlingDetailInfoDto > swaggerInfo 세팅
            detailInfoDto.setSwaggerInfo(swaggerInfo);

            // DTO 세팅
            detailResDto.setDetailInfo(detailInfoDto);
            detailResDtoList.add(detailResDto);
        }

        resDto.setDetail(detailResDtoList);

        driver.quit();

        return resDto;
    }

    /**
     * 셀레니움 크롬 옵션 세팅
     * @return
     */
    private ChromeOptions setChromeOptions() {
        ChromeOptions options = new ChromeOptions();        // 크롬 옵션 객체 생성

        options.addArguments("--headless=new");             // GUI 없는 리눅스 환경에서는 필수 (새로운 headless 모드 권장)
        options.addArguments("--window-size=1920,1080");    // headless 모드에서는 사이즈를 지정해야 일부 요소가 안보이는 문제 방지
        options.addArguments("--no-sandbox");               // 리눅스 root 환경에서 권한 문제 해결용
        options.addArguments("--disable-dev-shm-usage");    // /dev/shm 공유 메모리 부족 문제 방지 (특히 Docker 환경)

        // options.addArguments("--disable-gpu");                  // GPU 없는 환경에서 권장 (특히 오래된 서버나 Docker)
        // options.addArguments("--disable-software-rasterizer");  // GPU 가속 대신 CPU로 강제

        // options.addArguments("--remote-debugging-port=9222");   // 디버깅 필요할 때 유용
        // options.addArguments("--disable-extensions");           // 불필요한 확장 기능 차단
        // options.addArguments("--disable-dev-tools");            // 성능 최적화
        // options.addArguments("--single-process");               // 리소스 제한적인 컨테이너에서 안정성 ↑

        return options;
    }

    private String getInnerText(WebDriver driver, WebElement element) {
        return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", element);
    }

    /**
     * 상세기능 > 요청, 출력 파라미터 Parsing
     * @param tableDiv
     * @return
     */
    private List<OpenApiParamDto> parseParamTable(WebElement tableDiv) {
        List<OpenApiParamDto> paramList = new ArrayList<>();

        WebElement tbody = tableDiv.findElement(By.cssSelector("tbody"));
        List<WebElement> rows = tbody.findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));

            OpenApiParamDto dto = OpenApiParamDto.builder()
                .korNm(cols.get(0).getText().trim())
                .engNm(cols.get(1).getText().trim())
                .paramSize(cols.get(2).getText().trim())
                .required(cols.get(3).getText().trim())
                .sampleData(cols.get(4).getText().trim())
                .paramDesc(cols.get(5).getText().trim())
                .build();

            paramList.add(dto);
        }

        return paramList;
    }

}
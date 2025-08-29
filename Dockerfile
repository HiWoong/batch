# 1. JDK 기반 이미지 선택
FROM eclipse-temurin:17-jdk

# 2. 빌드 결과물 JAR 복사
COPY build/libs/batch-0.0.1-SNAPSHOT.jar app.jar

RUN apt-get update && apt-get install -y \
    wget unzip \
    libnss3 libxss1 libasound2t64 libx11-xcb1 \
    fonts-liberation libatk-bridge2.0-0 libgtk-3-0 \
    libdrm2 libgbm1 libxshmfence1 \
    xdg-utils libvulkan1 \
    && rm -rf /var/lib/apt/lists/*

# 크롬 설치
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get install -y ./google-chrome-stable_current_amd64.deb \
    && rm google-chrome-stable_current_amd64.deb

# 크롬드라이버 설치 (버전에 맞게 수정)
RUN wget -q https://storage.googleapis.com/chrome-for-testing-public/139.0.7258.154/linux64/chromedriver-linux64.zip \
    && unzip chromedriver-linux64.zip \
    && mv chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver \
    && rm -rf chromedriver-linux64 chromedriver-linux64.zip

# 3. 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=dev"]

# (선택) 컨테이너 포트 지정
EXPOSE 8080
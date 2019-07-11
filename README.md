# java-stock-ticker
Real-time stock tickers from the command-line based on Java

![java-stock-ticker](https://raw.githubusercontent.com/DaegiKim/java-stock-ticker/master/screenshot.gif)

## Requirements
- Java 10+
- Dependencies
  - org.apache.httpcomponents:httpclient:4.5.9
  - org.json:json:20180813

## Usage
First, build the project to generate the jar file.
```sh
# Single symbol with refresh interval 500 millis:
$ java -jar ./java-stock-ticker.jar 500 207760.KQ  
 
# Multiple symbols with refresh interval 500 millis:
$ java -jar ./java-stock-ticker.jar 500 ^KS11 ^KQ11 GOOG AAPL BTC-USD 207760.KQ 263750.KQ
```

#### Note
*Data on the US stock market is updated in real time, but most other countries has time delays(10~20min). [link](https://help.yahoo.com/kb/SLN2310.html)*

#
*Inspired by [ticker.sh](https://github.com/pstadler/ticker.sh)*
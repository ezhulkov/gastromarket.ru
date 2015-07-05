import org.apache.commons.io.IOUtils

def pageUrl = "http://hh.ru/search/resume?exp_period=all_time&area=1&text=%D0%BF%D0%BE%D0%B2%D0%B0%D1%80&pos=position&logic=normal&clusters=true&page="
def cookPages = []

def grabPages() {
    (1..249).each {
        def onePageUrl = pageUrl + it
        def onePageConnection = new URL(onePageUrl).openConnection() as HttpURLConnection
        onePageConnection.connect()
        def result = IOUtils.toString(onePageConnection.getInputStream()) as String
        def cid = result =~ "data-hh-resume-hash=\"([a-zA-Z0-9]*)\""
        def links = cid.collect().unique(false).stream().map { "http://hh.ru/resume/" + it[1] }.collect();
        cookPages.addAll(links)
        println it + " rc " + onePageConnection.responseCode + " cnt " + links.size()
    }

    new File("/tmp/hhpages.txt").withWriter { out ->
        cookPages.each {
            out.println it
        }
    }
}

def grabCook(pages) {
    pages.eachWithIndex { it, i ->
        def urlConnection = new URL("http://hh.ru" + it).openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("Host", "hh.ru")
        urlConnection.setRequestProperty("Cache-Control", "no-cache")
        urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36")
        urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.8,bg;q=0.6")
        urlConnection.setRequestProperty("Cookie", "regions=1; unique_banner_user=1436007711.156081015499585; hhtoken=zkcL!PxWjBbCekRlYEFtjmAgecyL; hhuid=MOClcIWuAUkpLFWEXN0oMg--; __gfp_64b=KdEQ2YE4zZS2a9B9O3c4QYE")
        urlConnection.connect()
        def result = IOUtils.toString(urlConnection.getInputStream()) as String
        def email = result =~ "<a.*itemprop=\"email\">(.*)<\\/a>"
        def gotEmail = false
        if (email.size() == 1 && email[0].size() == 2) {
            def file = new File("/Users/ezhulkov/Temp" + it);
            file.createNewFile();
            file.withWriter { out ->
                out.println result
            }
            gotEmail = true
        }
        println urlConnection.responseCode + " item:" + i + " email:" + gotEmail
    }
}

new File("hhpages.txt").eachLine { line ->
    cookPages.add(line)
}
grabCook(cookPages.drop(210))

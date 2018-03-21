import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils

import java.nio.charset.Charset

def letters = "абвгдеёжзийклмнопрстуфхцчшщьыъэюя"
def i = 18;
def set = new TreeSet();

for (char ch : letters.toCharArray()) {
    def urlConnection = new URL("http://eda.ru/RecipesCatalog/GetIncludeIngredients").openConnection() as HttpURLConnection
    def urlParameters = "term=" + ch;
    def postData = urlParameters.getBytes(Charset.forName("UTF-8"))
    def postDataLength = postData.length

    urlConnection.setRequestMethod("POST")
    urlConnection.setDoOutput(true)
    urlConnection.setDoInput(true)
    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    urlConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01")
    urlConnection.setRequestProperty("Host", "eda.ru")
    urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest")
    urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength))
    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/600.2.5 (KHTML, like Gecko) Version/8.0.2 Safari/600.2.5")
    urlConnection.getOutputStream().write(postData)
    urlConnection.getOutputStream().flush()

    urlConnection.connect()
    def result = IOUtils.toString(urlConnection.getInputStream()) as String

    def slurper = new JsonSlurper()
    def value = slurper.parseText(result)

    for (def el : value) {
        set.add(el.get("Name").toLowerCase());
    }
}

for (def el : set) {
//    println "insert into property_value (id,property_id,value) values (" + (i++) + ",2,'" + StringUtils.capitalise(el) + "');";
    println StringUtils.capitalise(el)
}



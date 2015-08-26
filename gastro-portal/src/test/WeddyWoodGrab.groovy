import org.apache.commons.io.IOUtils

def links = [
        "https://weddywood.ru/members/303_catering/",
        "https://weddywood.ru/members/kulinarnoe_soprovozhdenie_prazdnika_kejtering/",
        "https://weddywood.ru/members/megapolis_catering/",
        "https://weddywood.ru/members/osobennovkusno/",
        "https://weddywood.ru/members/tortik_annushka/",
        "https://weddywood.ru/members/hello_honey/",
        "https://weddywood.ru/members/cakecake_vkusneyshie_torty_na_zakaz/",
        "https://weddywood.ru/members/candy_cotton_shop/",
        "https://weddywood.ru/members/lavander_sweets/",
        "https://weddywood.ru/members/two_candy/",
        "https://weddywood.ru/members/the_foxy_nose/",
        "https://weddywood.ru/members/sladkiy_stol/",
        "https://weddywood.ru/members/moon_cream/",
        "https://weddywood.ru/members/no_name_cupcakes/",
        "https://weddywood.ru/members/konditerskaja_ravnovesie/",
        "https://weddywood.ru/members/astghiks_cakes/",
        "https://weddywood.ru/members/nocream/",
        "https://weddywood.ru/members/mon_bon/",
        "https://weddywood.ru/members/tortolyano/",
        "https://weddywood.ru/members/biscuitico/",
        "https://weddywood.ru/members/aa_sweet_pastries/",
        "https://weddywood.ru/members/so_apples/",
        "https://weddywood.ru/members/markushova_yuliya/",
        "https://weddywood.ru/members/butik_pechenya/"
]

println "EMAIL, PASSWORD, CATALOG, SOURCE"
links.eachWithIndex { link, i ->
    def urlConnection = new URL(link).openConnection() as HttpURLConnection
    urlConnection.connect()
    def result = IOUtils.toString(urlConnection.getInputStream()) as String
    def catalogName = (result =~ "<title>(.*)</title>")[0][1];
    catalogName = catalogName.substring(0, catalogName.length() - 3).replaceAll("&amp;", "&").replaceAll("&#039;", "'").replaceAll("&quot;", "'");
    def email = (result =~ "mailto:(.*?)\"")[0][1];
    def password = "03mba7"
    def source = link
    println email + "," + password + "," + catalogName + "," + source
}

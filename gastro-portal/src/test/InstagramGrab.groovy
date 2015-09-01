import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils

def tags = [
        "торт",
        "капкейк",
        "тортназаказ",
        "тортмск",
        "тортнаденьрождения",
        "тортнасвадьбу",
        "кейтеринг"
]

def filter = [
        "москва",
        "мск",
        "499",
        "495"
]

def cooks = []

def localCooks = []

def fetchCooks(tags) {
    def cooks = [:]
    tags.eachWithIndex { tag, i ->
        def mediaUrl = "https://api.instagram.com/v1/tags/" + tag + "/media/recent?access_token=39949484.1fb234f.15ef9534744f42b68c22cf3b86b82687%20%20";
        def depth = 0
        while (mediaUrl && depth++ < 100) {
            println "calling " + mediaUrl;
            def getMediaPage = new URL(mediaUrl).openConnection() as HttpURLConnection
            getMediaPage.connect()
            def result = IOUtils.toString(getMediaPage.getInputStream()) as String
            def medias = new JsonSlurper().parseText(result)
            mediaUrl = medias.pagination.next_url;
            medias.data.eachWithIndex { media, j ->
                cooks.get(media.user.id, []) << 1
            };
        };
    }
    return cooks.findAll { it.value.size > 1 }.collect { it.key }
}

def filterCooks(cooks, mskFilter) {
    def moscowCooks = []
    cooks.eachWithIndex { cook, i ->
        def userUrl = "https://api.instagram.com/v1/users/" + cook + "?access_token=39949484.1fb234f.15ef9534744f42b68c22cf3b86b82687%20%20";
        def getUserPage = new URL(userUrl).openConnection() as HttpURLConnection
        getUserPage.connect()
        def result = IOUtils.toString(getUserPage.getInputStream()) as String
        def userInfo = new JsonSlurper().parseText(result)
        def bio = userInfo.data.bio.toLowerCase()
        if (mskFilter.any { bio.contains(it) } && bio.contains("@")) {
            moscowCooks.add(userInfo.data.id)
        }
        println "done " + i
    }
    return moscowCooks;
}

def parseCooks(cooks) {
    println "EMAIL, PASSWORD, CATALOG, SOURCE"
    cooks.each {
        def userUrl = "https://api.instagram.com/v1/users/" + it + "?access_token=39949484.1fb234f.15ef9534744f42b68c22cf3b86b82687%20%20";
        def getUserPage = new URL(userUrl).openConnection() as HttpURLConnection
        getUserPage.connect()
        def result = IOUtils.toString(getUserPage.getInputStream()) as String
        def userInfo = new JsonSlurper().parseText(result)
        def emailRg = userInfo.data.bio =~ "(\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b)"
        if (emailRg.size() > 0) {
            def email = emailRg[0][0]
            def password = "3mba07"
            def sourceUrl = "https://instagram.com/" + userInfo.data.username
            def catalogName = userInfo.data.full_name
            if (catalogName.size() == 0) catalogName = userInfo.data.username
            println email + "," + password + "," + catalogName + "," + sourceUrl
        }
    }
}

cooks = fetchCooks(tags);
localCooks = filterCooks(cooks, filter)

parseCooks(localCooks)
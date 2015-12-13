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

def nicknames = [
        "uposhka",
        "alexaprettyfoxy",
        "marianna___k",
        "drachinskaya",
        "prekrasnayaya",
        "kydriashka_mc",
        "puuupsia",
        "vichgakiss",
        "lolasaifi",
        "julia_bugrova",
        "ann_azhishcheva",
        "marycenika",
        "magicholida",
        "photo_inyusha",
        "kkozmina",
        "julie_gaika",
        "agatha_moritz",
        "varvara_kryu",
        "mashamashkin",
        "elisemikhailova",
        "quizas_is_love",
        "alchena_rumyanceva",
        "anya_zaidentsal",
        "annamalik11",
        "vinjulia13",
        "vlada_stormal",
        "olya_savina",
        "kristinka_mandarinka26",
        "kudri_mari",
        "alena_roshior",
        "sandra_kazachkova",
        "hallwood3",
        "romanticnadiagirl",
        "photonikonova",
        "westsidetobe",
        "sonika_",
        "floartdeco",
        "ekakucher",
        "danaya_prigozhina",
        "eleonorkaa",
        "_lukaa_",
        "lukeria10",
        "kate_tsareova",
        "viktori_chistova",
        "spirina_a",
        "ksushaoseyuk",
        "dragi_92",
        "ksnatalia",
        "nice.owl",
        "annie20743",
        "mistafsi_",
        "viavikss",
        "e_andreewna",
        "ekaterinamerkulova",
        "anastasia1713",
        "maria_budilina",
        "nastjasheremetjeva",
        "arinaolegovna9",
        "katya_shashkina",
        "lise4ek",
        "sashachaplygina",
        "oksana1441",
        "seleba_loku",
        "diana.babaeva",
        "alieva_gyunay",
        "anastasiajuravleva",
        "daria_sokol_2001",
        "daria_mitsevich",
        "enzekreushka",
        "natasha_reutov",
        "g.rts",
        "vs.jones",
        "ann.potapova",
        "katenokmrrr",
        "yurevnae",
        "it_is_a_kind_of_magic",
        "rastipopa1",
        "sladkadecor",
        "marialisenkova",
        "katerina5315",
        "sunliteflower",
        "cakejulia",
        "culinary_academy_exclusive"
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

def nickToId(nicks) {
    nicks.each {
        def userUrl = "https://api.instagram.com/v1/users/search?q=" + it + "&access_token=39949484.1fb234f.15ef9534744f42b68c22cf3b86b82687%20%20";
        def getUserPage = new URL(userUrl).openConnection() as HttpURLConnection
        getUserPage.connect()
        def result = IOUtils.toString(getUserPage.getInputStream()) as String
        def userInfo = new JsonSlurper().parseText(result)
        if (userInfo.data[0] != null) println userInfo.data[0].id
    }
}

//cooks = fetchCooks(tags);
//localCooks = filterCooks(cooks, filter)
//parseCooks(localCooks)

nickToId(nicknames)
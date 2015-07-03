import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils

def tags = [
        "торт",
        "капкейк",
        "тортназаказ",
        "кейтеринг"
]

def mskFilter = [
        "москва",
        "moscow",
        "msk",
        "мск",
        "495",
        "499"
]

def cooks = [1538733845, 659401230, 307241643, 267481568, 28132984, 1511274487, 1639903617, 1501886416, 291168058, 1553911162, 1557240686, 1940077258, 2087414343, 310665001, 24238720, 1623165280, 1166423833, 1552068616, 1548156221, 1580588646, 329590804, 1144860533, 1257772440, 798198832, 1360791195, 296086603, 2123372482, 1494922910, 2085778417, 2012537110, 1451003398, 1492842721, 184703599, 391841780, 3395529, 1632094792, 218660686, 1724870979, 1499102884, 2039345838, 470981652, 194005237, 285131235, 1962142018, 546276831, 2133420820, 2032220095, 1153530319, 206448953, 190095730, 1391819442, 2129224056, 52006579, 627503439, 1655642787, 537673457, 476535659, 1822147681, 1541938532, 1337067155, 1134543146, 1495889139, 1655061891, 1912558190, 1024230334, 1006289886, 2084001603, 2057785151, 469447364, 1343928196, 2064812718, 768007829, 1463489325, 233476837, 1944199218, 986433395, 1224779167, 190676281, 1550870541, 1419278837, 610664334, 816240633, 1926974556, 1592847192, 1491609073, 1819604052, 2101007416, 1049874053, 412815819, 2128597732, 297622798, 1822349586, 1620811141, 1171447329, 1242287227, 1361044649, 1800105437, 922223485, 226069666, 1767712467, 1258295967, 214000490, 25723973, 1129084107, 1449081302, 1448237350, 1328004816, 1439814067, 237118369, 1414797079, 497579858, 230077291, 1498387140, 17925114, 536320515, 784608761, 1806282883, 1564828157, 1642594747, 2010019269, 1190674389, 1238516374, 1492822906, 251164023, 1618068142, 233582941, 1594382100, 269963108, 1403372146, 2137416473, 298718942, 1424097084, 861587064, 1913229509, 197497694, 1538418309, 1529819625, 2082034327, 575993886, 235321903, 529659630, 1620187824, 53332427, 342967479, 1769849389, 282916172, 1632344382, 921631697, 1542831570, 584289508, 705326726, 1468855978, 1911803057, 1507498439, 1967817026, 1371067902, 1411687227, 1354547304, 1393772133, 399370846, 408104339, 595084941, 1295922792, 1075976007, 1201009296, 558785895, 719693021, 1648235401, 1637079805, 1041502098, 450342638, 1611154281, 1497311662, 1986609443, 1031268294, 1999622371, 415484680, 1617608333, 301103156, 2066220937, 1481983088, 1678674694, 1640237385, 245098563, 1551418665, 1375060142, 29702595, 352586781, 1984615521, 546971658, 1551926112, 965192898, 1808092691, 282730646, 516336689, 1098484244, 535993735, 1522719889, 1961020634, 997058116, 2056465565, 846449489, 1326658501, 1651209092, 41234179, 499731429, 226829141, 1961021796, 1772384067, 1278462851, 1087852894, 1495992391, 280508177, 1965665610, 487116899, 1031642904, 550294780, 1707789265, 605229516, 1636476792, 1777495060, 2139272153, 1325548791, 1163096720, 1600043761, 2041623220, 1195516931, 2053690599, 1982204658, 1655585211, 1210926726, 2053705545, 1207495804, 565233499, 1440733049, 756255464, 2060128152, 1760800292, 1431893678, 292404894, 1346212509, 341781608, 589849315, 340780969, 1495031806, 1457027185, 777144317, 1564333689, 536235341, 1090621814, 1680810735, 639351701, 268912162, 437564827, 1240758075, 265888972, 288149944, 1550920212, 1543662523, 1573222798, 306678285, 1232400258, 205874943, 538350063, 1473912819, 693538092, 1938596198, 1825286412, 1597358602, 581186077, 1812997418, 1155286064, 286790185, 1284866273, 1261390132, 1594052602, 1755845570, 194872756, 1682489264, 1643819353, 1741234595, 1523892682, 1382746768, 418073799, 1815949347, 378658704, 1353280556, 327871653, 1366820413, 25936794, 305313889, 1540369688, 1465219562, 517411708, 413311932, 1945017215, 1689481495, 2092983771, 1511458708, 183410264, 1490703054, 778176129, 1987820834, 1403047405, 51050432, 1577106018, 2064140455, 2116608798, 709757483, 1689474704, 2090574894, 1310007841, 2062034773, 1066267499, 274343471, 427790203, 1692014566, 1980203706, 1458228918, 283004480, 1517941538, 1474723628, 479203319, 1228594524, 2060563436, 532544409, 582304606, 240559596, 1984064879, 1956429625, 2913252, 2037775682, 615753302, 10199966, 1812855832, 1465132354, 1760630721, 1140360267, 1741274742, 1227914002, 757214159, 2052996474, 1583746749, 1694159964, 1366846600, 375466242, 1955002669, 1421498522, 214931367, 1069122594, 235969656, 336091650, 1699317778, 257740848, 981535111, 521569902, 392318874, 1603053757, 2055408201, 1804086559, 1507169128, 511895889, 1435606356, 554791965, 1955001225, 1533081140, 547360708, 733862525, 221913291, 480511932, 1540232065, 1176173577, 1565562079, 1677976081, 2089802816, 1560750992, 2035325178, 1637609645, 571266493, 1589263056, 1298381252, 1371801135, 1542579643, 2114221024, 1557369962, 1787155422, 412019193, 1751116631, 1662248603, 180203535, 1520098382, 626593121, 28335699, 709872508, 1095541715, 2118441332, 535957727, 534083508, 1807579480, 1416748895, 1726073116, 1303136495, 1165927234, 1421261423, 1617858910, 285724217, 2042138056, 259309734, 548979651, 1306415102, 1912602644, 1675510574, 1403830437, 376907675, 306855040, 337658551, 239729417, 303989016, 1964621393, 55555455, 1573239168, 1601152157, 1517950663, 985635456, 2012179228, 2084181238, 1338010185, 297610919, 1442205133, 1692026349, 1522917411, 1699482398, 1708068028, 2087632151, 318357511, 1986605172, 584076191, 1465324270, 673928468, 1421070334, 1579988336, 381830113, 2081307676, 1594243398, 1465295612, 1779868135, 1516881546, 1966865908, 1739346903, 340435826, 817545809, 1715161821, 665792807, 1252700928, 416081195, 685916630, 186440480, 2009615677, 1500733717, 494161390, 1923774213, 190984394, 1390333592, 1635817059, 489200651, 1780022991, 2126198695, 850535270, 334031347, 1649621416, 1386803162, 1303004633, 262994048, 415620520, 1837831242, 2012592564, 1980149081, 1359503995, 1525289469, 1412956569, 1536726712, 2043004830, 365625788, 1547542938, 1405879732, 232551950, 478928013, 1556024580, 306913989, 2128456283, 1307251863, 1561357025, 401094645, 1342791362, 1734872822, 2131315523, 11885573, 1053887349, 1452890183, 982246155, 1143263000, 425339456, 2100083284, 996087602, 1653251051, 1524893412, 41419047, 232449774, 1493627695, 1986423580, 1952942438, 1564499897, 2135689671, 1303310641, 1400917517, 2054570404, 2046812977, 1341393749, 1638682132, 332596380, 1776297155, 1684156670, 1942841278, 1732492101, 1709866526, 1692098458, 218260109, 2105818571, 1572623432, 1728874657, 1814412495, 1774790005, 1761528322, 332116160, 1548766491, 1721055864, 1771310459, 290070450, 1316648583, 1810177751, 399839234, 1462876138, 2097672330, 2099726258, 1769302096, 1492310274, 1555208156, 270048070, 1757211832, 1429873871, 1502705460, 1695342391, 2138008876, 1523025494, 597176574, 1493845445, 400220057, 385453995, 211960426, 2036936156, 1364475942, 225013369, 1375139595, 1148356512, 346754260, 1508793149, 1534395473, 2061420652, 1746802314, 2025819005, 536135418, 1831658005, 2046143136, 173615039, 522268012, 1565753354, 1749767927, 1708282544, 1972550619, 1723922805, 216730776, 1636368195, 1525295003, 364514202, 1484400350, 1587238207, 1705972212, 1561176773, 608866343, 1712960906, 307942394, 1415603336, 1385246441, 1546622260, 21038854, 2106511115, 1990849889, 1497155589, 1687375546, 1577236779, 472607582, 2137818589, 547583702, 1967699352, 1928218927, 1752538334, 1805660289, 2038201798, 1934614739, 344752959, 2041018425, 1509090478, 1906136115, 1796572818, 1428371618, 1193557650, 2094020727, 1910218591, 2139406897, 2064487067, 1719051936, 1680342915, 1109527886, 249875836, 864694959, 2001344508, 1590380081, 1838431705, 175650103, 1963737730, 1578215562, 1580038894, 267492812, 2137207902, 1961799698, 1061992971, 1582764014, 1513044115, 1345223499, 1755135888, 1905766709, 560445775, 510738666, 385953845, 47850177, 179418859, 402133944, 343149269, 1309714744, 1514741828, 1074078888, 1974181877, 1414593973, 1412967432, 1581635943, 2092603388, 2018607387, 326638924, 1785522093, 1729932113, 353465067, 1917243671, 529554767, 2126207121, 1711753985, 1554492533, 1201199484, 756926704, 715150116, 210223154, 308419350, 1020704299, 1644429976, 1565060005, 37633617, 434300007, 2044082446, 1774068835, 892959548, 1134448814, 1816167675, 292437176, 489233526, 1532506280, 506371916, 712695288, 1571063022, 4831145, 1596637313, 1379904823, 2108405227, 1536456159, 1687109484, 1015579134, 1784839306, 1306891882, 288054113, 508760405, 1806314072, 527825376, 1916125141, 240971687, 1184610952, 1472767068, 1289932452, 214204103, 1948189096, 1806582695, 2106578675, 2045010372, 1409871662, 1941349975, 474054294, 2082123920, 2107280144, 280601638, 1545505345, 1824851234, 2079270146, 1723687259, 1798544932, 315414576, 951767685, 1720586328, 1499809459, 399725381, 1680215083, 303979894, 1480309517, 1419244814, 729221658, 1969007196, 1923407330, 1148560460, 6322500, 1492979822, 622154711, 1901710162, 1410663337, 1314607926, 474052817, 447249020, 1641269694, 202676206, 289574533, 1728633358, 301241350, 1295061201, 1393302871, 1966203440, 502171607, 3192058, 25188774, 419708364, 1477833483, 1902487099, 549084500, 183787832, 1799737577, 1594625372, 354872799, 2022438232, 286052931, 1972042743, 344932188, 1936578464, 1697709851, 511586907, 1481794815, 314933018, 40610187, 1007685952, 1395082845, 1660516610, 530864166, 2029307941, 1972597679, 822463916, 607252933, 258836706, 1727135864, 1564542745, 1559711636, 1215825677, 1798348603, 1413166223, 953098398, 1769946122, 1811723697, 29828142, 2086003351, 442592470, 1499679073, 551241194, 732341076, 1480134098, 2070529217, 571016116, 1384739338, 1562840261, 1922992439, 247638757, 2070976178, 144891052, 253444609, 2128623413, 582401158, 273287950, 1952023733, 1270020346, 323047903, 1687514292, 1640168609, 356627194, 1184401659, 1587858896, 310265572, 178523038, 678589833, 450478196, 1742951251, 1403745246, 1725074217, 1737369387, 1405158198, 1837645205, 1779689921, 1680326139, 331151899, 1623857982, 1273065367, 2037387771, 2045733895, 576430127, 1657229311, 1421036346, 2104505989, 976429477, 1398631231, 229815446, 1514886471, 468376216, 1334425514, 480548197, 350655880, 1536012973, 845479159, 1410760750, 1439059385, 1414405176, 1704954174, 1319911805, 1638827880, 234028755, 1541643893, 1720626763, 1918115060, 1207349659, 567428461, 1907648988, 1091300812, 291499896, 355669704, 1799795787, 773602669, 1475643822, 1996222304, 1719993365, 2122115872, 356225579, 269892940, 1181895312, 1115492022, 675658155, 1420689190, 372150503, 1554352082, 358806936, 1653652248, 2110174300, 1743392674, 520483419, 1956910081, 2122200282, 1682187874, 207769345, 1267388883, 1560699816, 1693727194, 1629080847, 312561887, 1044109187, 1607186283, 1768996432, 1264511953, 1202805134, 1652414408, 2132516404, 751560411, 372854156, 1829413895, 358044056, 285554476, 144163847, 1118782297, 22610498, 710055565, 46495253, 1723133702, 1646221310, 333342443, 1463287886, 2027416604, 2119215777, 2109020830, 1414005873, 1463993041, 634754003, 319599453, 2112840889, 403226035, 1810905150, 687707490, 1008892582, 1716336782, 1485432821, 683429767, 2832010, 173591658, 221383138, 19081842, 1594012087, 1736134811, 259631608, 1775986179, 1527822498, 1535910544, 1946600841, 1784804720, 1695976814, 1450658451, 467750996, 1603288999, 2099815335, 351664523, 417771513, 1216040438, 1709838008, 211359194, 1546484076, 1509844153, 973543162, 2084459563, 1502679740, 557268478, 416095233, 195493769, 1958557701, 1734057036, 2079818996, 1591244236, 1065377151, 1983818899, 1819124270, 200062230, 611874960, 1517748165, 420436201, 1240660596, 1596732116, 2136913466, 1518825681, 250266924, 1823020310, 1540857226, 652384563, 1591387458, 269326062, 1552252673, 1768860964, 1366624830, 2116516500, 1298455121, 1424046055, 2065751910, 1523943111, 609613280, 1824907036, 1564911238, 1414302581, 1735273695, 1680471449, 1590150425, 1337065349, 1713129075, 338426360, 1907684485, 1647705572, 1950950978, 224385198, 1994840323, 1713153040, 1949705385, 231633764, 280739261, 1641225269, 1753183150, 1496063967, 2022134104, 2012497326, 348151094, 2026870581, 587920500, 480917184, 1419250582, 1256164528, 1483543805, 1688819123, 1471268353, 2119229603, 1432064476, 563083715, 234143351, 245124678, 1717523319, 1652291589, 2122096708, 1511305024, 1387324999, 1573390009, 1264661600, 2135328944, 1677668951, 1722072233, 1805315890, 1583297708, 1095944928, 315465341, 1754729693, 781845930, 1273831211, 1309024504, 335066394, 208090777, 287425508, 2000088165, 1914044807, 835773, 508778838, 411734924, 411669325, 1590515791, 1802318713, 2117709955, 1928917738, 1709784337, 822056033, 1313806095, 1970205564, 288552613, 1909414433, 903860913, 1564426884, 2094214431, 1161787629, 1683401571, 2116174962, 2125318733, 1964412666, 1450507444, 1040358718, 2032598296, 1315262175, 1465202091, 2084493783, 2107228141, 1056287401, 1750254996, 1362810524, 1186573181, 1832184593, 2022495891, 1835467396, 1590080895, 218566506, 1525877150, 221117669, 2030501166, 2136124280, 1535413724, 1396319379, 1986123954, 1584497428, 1495776547, 2036121886, 540646813, 261305343, 437807634, 842427, 514935759, 1222770681, 236169117, 1405227168, 1347262646, 1808047200, 1490636935, 1395011972, 1303295890, 1530305320, 384342991, 538430348, 1565553184, 1211016117, 538949629, 2084404831, 2121221115, 1785326631, 783666028, 452324357, 1229272271, 545877302, 336902445, 1707647250, 1251580019, 228251918, 358761180, 1559877523, 966128669, 2044133921, 1960585217, 2010578870, 1528570736, 2021689251, 220364714, 1524805248, 1432727371, 1461679201, 1697088913, 1835792359, 1677758447, 1559633349, 2060555813, 1425204966, 1791331564, 1329850961, 1997621583, 1292405342, 1445112619, 1982537846, 621313129, 1914446469, 1624179166, 1369268398, 283505365, 1562391492, 1104397933, 1240619434, 1626717637, 596177629, 2048065394, 1590338294, 746018429, 1413927211, 675400488, 52893647, 1277309512, 447438273, 988398752, 193033189, 16254939, 1440428514, 1573247437, 1482257084, 1453455184, 630866696, 646046678, 43034011, 1612433706, 1267694689, 2069325107, 1297959213, 288601927, 200090525, 373144539, 1453954481, 538249632, 902388371, 1929765350, 927925878, 2024035984, 503710015, 458528103, 1501759433, 1623552416, 1956818794, 940985863, 1358580193, 1671032364, 2121665706, 2052841298, 1273191168, 1502440717, 1245476298, 485388725, 316007074, 2100402168, 196910084, 1743799520, 1578889645, 1514470114, 1210087310, 1799128899, 1542036238, 555269022, 1450323729, 525251528, 1542578448, 2117685048, 1680129338, 722541150, 1534911840, 1738735475, 1565421182, 1550741658, 1689032146, 1193619216, 416837241, 234110290, 1905722282, 1284466964, 305350586, 1465441875, 396000040, 1701872834, 1299993521, 1988944569, 640076254, 1927744630, 1647543244, 1189883867, 1543514365, 1721673908, 1441129982, 1770098347]

def mskCooks = [28132984, 1501886416, 3395529, 1024230334, 190676281, 1911803057, 301103156, 352586781, 846449489, 1490703054, 1984064879, 1565562079, 571266493, 1589263056, 626593121, 534083508, 1338010185, 1923774213, 415620520, 1547542938, 401094645, 2046812977, 1709866526, 1548766491, 1462876138, 2099726258, 225013369, 1565753354, 1961799698, 1513044115, 179418859, 1074078888, 1816167675, 1499809459, 3192058, 269892940, 319599453, 221383138, 19081842, 1695976814, 250266924, 1540857226, 1677668951, 1309024504, 822056033, 1396319379, 621313129, 2024035984, 1671032364, 316007074, 1770098347]

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
//    println "EMAIL, PASSWORD, CATALOG, SOURCE"
    cooks.each {
        def userUrl = "https://api.instagram.com/v1/users/" + it + "?access_token=39949484.1fb234f.15ef9534744f42b68c22cf3b86b82687%20%20";
        def getUserPage = new URL(userUrl).openConnection() as HttpURLConnection
        getUserPage.connect()
        def result = IOUtils.toString(getUserPage.getInputStream()) as String
        def userInfo = new JsonSlurper().parseText(result)
        def password = "3mba07"
        def sourceUrl = "https://instagram.com/" + userInfo.data.username
        def catalogName = userInfo.data.full_name
        if (catalogName.size() == 0) catalogName = userInfo.data.username
        println email
    }
}

//fetchCooks(tags);
//filterCooks(cooks, mskFilter)

parseCooks(mskCooks)
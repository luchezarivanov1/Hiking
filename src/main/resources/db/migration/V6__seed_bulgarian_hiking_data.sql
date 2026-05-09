-- V6: Add missing columns to mountain and hut tables, then seed Bulgarian hiking data

-- ============================================================
-- 1. SCHEMA EXTENSIONS
-- ============================================================

ALTER TABLE mountain
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS latitude    DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude   DOUBLE PRECISION;

ALTER TABLE hut
    ADD COLUMN IF NOT EXISTS elevation_m  INTEGER,
    ADD COLUMN IF NOT EXISTS latitude     DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude    DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS has_restaurant    BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS has_accommodation BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS phone        VARCHAR(50);

-- ============================================================
-- 2. MOUNTAINS (20 entries)
-- ============================================================
-- highest_peak column stores elevation in meters of the highest peak

INSERT INTO mountain (name, region, highest_peak, description, latitude, longitude) VALUES
-- Rila
('Rila',
 'Kyustendil / Blagoevgrad / Sofia',
 2925.0,
 'The highest mountain in Bulgaria and on the Balkan Peninsula. Rila covers an area of approximately 2629 km² and is renowned for its glacial lakes, dramatic granite peaks, dense coniferous forests, and the famous Rila Monastery. The highest point is Musala (2925 m), which is also the highest summit in the Balkans.',
 42.1908, 23.5875),

-- Pirin
('Pirin',
 'Blagoevgrad',
 2914.0,
 'Pirin is a UNESCO World Heritage mountain in southwest Bulgaria, characterised by over 70 glacial lakes, marble and granite rock formations, and 118 peaks above 2000 m. The highest summit is Vihren (2914 m). The mountain is a national park renowned for ancient Bosnian pine (Pinus heldreichii) forests.',
 41.7700, 23.4400),

-- Rhodopes (Rhodopi)
('Rhodopes',
 'Plovdiv / Smolyan / Kardzhali / Pazardzhik',
 2191.0,
 'The Rhodopes (Rhodopi) are the largest mountain range in Bulgaria, stretching over 14 700 km². Known for dense forests, dramatic gorges (Trigrad, Buynovsko), mystical caves, and rich Thracian heritage. The highest peak is Golyam Perelik (2191 m). The western Rhodopes contain the Shiroka Laka and Momchilovtsi villages.',
 41.6800, 24.6500),

-- Stara Planina (Balkan)
('Stara Planina (Balkan)',
 'Multi-province – Central Bulgaria',
 2376.0,
 'Stara Planina (the Old Mountain) is the longest mountain range in Bulgaria, crossing the entire country from northwest to southeast for about 560 km. The highest peak is Botev (2376 m). The range forms the traditional watershed between northern and southern Bulgaria and is home to numerous eco-trails and the Central Balkan National Park.',
 42.7200, 25.1500),

-- Vitosha
('Vitosha',
 'Sofia',
 2290.0,
 'Vitosha is a nature park located immediately south of Sofia, making it one of the most accessible mountains in Bulgaria. Its highest point is Cherni Vrah (2290 m). The mountain is famous for its stone rivers (morena fields) and has been a nature park since 1934 – the oldest nature park in Bulgaria.',
 42.5200, 23.2900),

-- Sredna Gora
('Sredna Gora',
 'Sofia / Plovdiv / Stara Zagora',
 1604.0,
 'Sredna Gora (Middle Forest) runs parallel to Stara Planina from west to east. The highest peak is Bogdan (1604 m). The mountain is historically significant as the site of the Shipka Pass battles during the Russo-Turkish War of 1877–78. It contains the Koprivshtitsa historical village.',
 42.6000, 24.3000),

-- Slavyanka (Ali Botush)
('Slavyanka',
 'Blagoevgrad',
 2212.0,
 'Slavyanka (also known as Ali Botush) is a small but dramatic mountain in southwest Bulgaria near the town of Gotse Delchev. Its highest point is Golyam Kilim (2212 m). It is known for a reserve protecting the Macedonian pine (Pinus peuce) and the Slavyanka cave system.',
 41.3800, 23.6000),

-- Osogovo
('Osogovo',
 'Kyustendil',
 2252.0,
 'Osogovo (Osogovska Planina) straddles the border between Bulgaria and North Macedonia. The highest Bulgarian summit is Ruen (2252 m). The mountain is covered by coniferous forests and offers spectacular views over the Struma and Pchelinsko valleys.',
 42.1700, 22.5500),

-- Belasitsa
('Belasitsa',
 'Blagoevgrad',
 1880.0,
 'Belasitsa is a low granite mountain in southwestern Bulgaria near Petrich. Its highest point is Radomir (1880 m). The mountain is notable for its chestnut forests — one of the largest sweet chestnut forests in Bulgaria — and its mild microclimate.',
 41.3600, 23.0200),

-- Kraishte / Konyavska Planina
('Konyavska Planina',
 'Kyustendil',
 1487.0,
 'Konyavska Planina is a gentle mountain west of Kyustendil. Its highest point is Konyavska Chuka (1487 m). It is traversed by the Kom–Emine long-distance trail and is known for its pastoral landscapes and spring flora.',
 42.4500, 22.7800),

-- Vrachanska Planina
('Vrachanska Planina',
 'Vratsa',
 2166.0,
 'Vrachanska Planina (Vratsa Balkan) is a nature park in northwest Bulgaria famed for the spectacular Vratsata gorge – one of the longest rock gorges in Bulgaria. The highest peak is Vola (2166 m). The Ledenika cave system is situated at its foothills.',
 43.1900, 23.5500),

-- Shumen Plateau (Shumenska Planina)
('Shumenska Planina',
 'Shumen',
 504.0,
 'Shumenska Planina is a limestone plateau in northeast Bulgaria above the city of Shumen. Its highest point is 504 m. It hosts the Shumen Fortress, the Tombul Mosque (one of the largest mosques in the Balkans), and important Thracian and medieval monuments.',
 43.2900, 26.9300),

-- Strandzha
('Strandzha',
 'Burgas',
 1031.0,
 'Strandzha is a mountain in southeast Bulgaria along the Black Sea coast, covered by the oldest and most biologically diverse broadleaf forests in Bulgaria. The highest peak is Mahya (1031 m). Strandzha Nature Park (the largest nature park in Bulgaria at 1161 km²) protects ancient Pontian species of flora and fauna.',
 42.1000, 27.5000),

-- Sashtinska Sredna Gora (Ihtimanska)
('Ihtimanska Sredna Gora',
 'Sofia / Plovdiv',
 1549.0,
 'Ihtimanska Sredna Gora is the eastern section of Sredna Gora, stretching between Ihtiman and Plovdiv. Its highest point is Golishte (1549 m). The area is mainly forested with oak and beech and offers relatively easy hiking trails connecting the Sofia and Plovdiv basins.',
 42.4500, 23.8500),

-- Rila – Malyovitsa sub-range
('Malyovitsa',
 'Blagoevgrad (Rila National Park)',
 2729.0,
 'Malyovitsa is a sub-range within Rila National Park, centred around the Malyovitsa hut complex. The highest point is Malyovitsa Peak (2729 m). The area is extremely popular for day hikes to glacial lakes and technical climbs on the Malyovitsa rock faces.',
 42.1500, 23.3500),

-- Plana Planina
('Plana Planina',
 'Sofia',
 1253.0,
 'Plana Planina is a small mountain southeast of Sofia between Vitosha and Rila. Its highest point is Golam Rezen (1253 m). It is predominantly forested with oak and pine and is used for weekend hiking by Sofia residents.',
 42.3800, 23.5200),

-- Lozenska Planina
('Lozenska Planina',
 'Sofia',
 1272.0,
 'Lozenska Planina is a low mountain south of Sofia near Pancharevo Lake. The highest point is 1272 m. The mountain offers easy trails through oak forests and is known for the rock phenomenon Skakavitsa waterfall.',
 42.4500, 23.5500),

-- Besaparski Ridove
('Besaparski Ridove',
 'Plovdiv',
 589.0,
 'Besaparski Ridove (Besapara Ridges) is a hilly ridge west of Plovdiv along the Maritsa river. The highest point is 589 m. The area contains the Kleptuza spring, medieval rock churches, and the village of Perushtitsa.',
 42.1200, 24.2000),

-- Rila – Skakavitsa area / Borovec
('Borovets Area (Rila)',
 'Sofia (Samokov)',
 2925.0,
 'The Borovets area refers to the northeastern slopes of Rila around Bulgaria''s oldest mountain resort (Borovets). The area provides access to the Musala peak via the Yastrebets gondola and is extensively used for skiing and summer hiking. It lies within Rila National Park.',
 42.2600, 23.6000),

-- Balkanets (Central Balkan sub-range)
('Central Balkan',
 'Gabrovo / Lovech / Troyan / Karlovo',
 2376.0,
 'Central Balkan (Tsentralen Balkan) is a national park in the heart of Stara Planina. It protects the Boatin, Tsarichina, Dzhendema, Sokolna, Peyne, Kozya Stena, and Steneto reserves. The highest summit is Botev (2376 m). The park is home to bears, wolves, lynx, golden eagles, and chamois.',
 42.7500, 24.9000);

-- ============================================================
-- 3. HUTS (50 entries)
-- Uses a VALUES + JOIN on mountain name to avoid hardcoded IDs
-- ============================================================

INSERT INTO hut (name, address, capacity, open_year_round, rating, mountain_id,
                 elevation_m, latitude, longitude, has_restaurant, has_accommodation, phone)
SELECT v.name, v.address, v.capacity, v.open_year_round, v.rating, m.id,
       v.elevation_m, v.latitude, v.longitude, v.has_restaurant, v.has_accommodation, v.phone
FROM (VALUES
  -- RILA
  ('Hut Musala'::text,                  'Rila National Park, near Musala Peak'::text,                   120::int, TRUE::bool,  4.2::float8, 'Rila'::text,                   2389::int, 42.1760::float8, 23.5887::float8, TRUE::bool,  TRUE::bool,  '+359 7071 2345'::text),
  ('Hut Ivan Vazov',                    'Rila National Park, Musalenski Lakes area',                    130,      FALSE,       4.5,         'Rila',                         2369,      42.1720,         23.5760,         TRUE,        TRUE,        '+359 899 123456'),
  ('Hut Malyovitsa',                    'Rila National Park, Malyovitsa area',                          170,      TRUE,        4.4,         'Rila',                         1729,      42.1502,         23.3510,         TRUE,        TRUE,        '+359 7071 2560'),
  ('Hut Sedemte Ezera (Seven Lakes)',   'Rila National Park, Seven Rila Lakes cirque',                  120,      FALSE,       4.6,         'Rila',                         2198,      42.1963,         23.3245,         TRUE,        TRUE,        '+359 896 678901'),
  ('Hut Ribni Ezera',                   'Rila National Park, Ribni Ezera lakes',                         85,      FALSE,       4.1,         'Rila',                         2230,      42.1500,         23.4300,         TRUE,        TRUE,        NULL::text),
  ('Hut Skakavitsa',                    'Rila, Skakavitsa area, near Borovets',                          60,      TRUE,        3.9,         'Rila',                         1750,      42.2650,         23.5740,         TRUE,        TRUE,        '+359 888 345678'),
  ('Hut Yastrebets',                    'Rila National Park, Yastrebets area, Borovets',                200,      TRUE,        4.3,         'Rila',                         2369,      42.2210,         23.6020,         TRUE,        TRUE,        '+359 7071 2400'),
  ('Hut Granchar',                      'Rila, Granchar locality',                                       50,      FALSE,       3.8,         'Rila',                         1760,      42.1930,         23.4100,         FALSE,       TRUE,        NULL),
  ('Hut Rilski Ezera',                  'Rila National Park, western sector',                            80,      FALSE,       4.0,         'Rila',                         2020,      42.1740,         23.3050,         FALSE,       TRUE,        NULL),
  ('Hut Parangalitsa',                  'Rila National Park, Parangalitsa reserve',                     30,      FALSE,       3.7,         'Rila',                         1545,      42.0500,         23.2800,         FALSE,       TRUE,        NULL),
  ('Hut Kartala',                       'Rila, Kartala locality',                                        45,      FALSE,       3.8,         'Rila',                         1460,      42.1350,         23.6400,         FALSE,       TRUE,        NULL),
  ('Hut Mechit',                        'Rila National Park, Mechit area',                               30,      FALSE,       3.5,         'Rila',                         2100,      42.1100,         23.4600,         FALSE,       TRUE,        NULL),
  -- PIRIN
  ('Hut Vihren',                        'Pirin National Park, below Vihren Peak',                       130,      FALSE,       4.5,         'Pirin',                        1950,      41.7694,         23.4072,         TRUE,        TRUE,        '+359 7444 2356'),
  ('Hut Bezbog',                        'Pirin National Park, Bezbog Lake area',                        130,      FALSE,       4.3,         'Pirin',                        2236,      41.6820,         23.3190,         TRUE,        TRUE,        '+359 886 234567'),
  ('Hut Tevno Ezero',                   'Pirin National Park, near Tevno Lake',                          40,      FALSE,       4.1,         'Pirin',                        2516,      41.7280,         23.4550,         FALSE,       TRUE,        NULL),
  ('Hut Banderitsa',                    'Pirin National Park, Banderitsa area, above Bansko',            100,      FALSE,       4.2,         'Pirin',                        1810,      41.7920,         23.4600,         TRUE,        TRUE,        '+359 886 567890'),
  ('Hut Demyanitsa',                    'Pirin National Park, Demyanitsa valley',                        90,      FALSE,       4.0,         'Pirin',                        1895,      41.7100,         23.3800,         TRUE,        TRUE,        NULL),
  ('Hut Pirin',                         'Pirin National Park, central section',                          80,      FALSE,       4.2,         'Pirin',                        1966,      41.7660,         23.5600,         TRUE,        TRUE,        '+359 887 901234'),
  ('Hut Gotse Delchev',                 'Southern Pirin, near Gotse Delchev',                            70,      FALSE,       3.7,         'Pirin',                        1210,      41.5800,         23.7300,         FALSE,       TRUE,        NULL),
  ('Hut Yane Sandanski',                'Pirin, near Sandanski',                                         60,      FALSE,       3.8,         'Pirin',                        1250,      41.5200,         23.2900,         TRUE,        TRUE,        NULL),
  ('Hut Orelek',                        'Pirin National Park, Orelek area',                              40,      FALSE,       3.9,         'Pirin',                        2070,      41.7400,         23.5100,         FALSE,       TRUE,        NULL),
  ('Hut Kamenitsa',                     'Pirin National Park, Kamenitsa Lake area',                      60,      FALSE,       4.2,         'Pirin',                        2100,      41.7550,         23.4850,         FALSE,       TRUE,        NULL),
  -- RHODOPES
  ('Hut Persenk',                       'Rhodopes, near Persenk Peak',                                   60,      TRUE,        3.8,         'Rhodopes',                     1930,      41.7640,         24.5600,         TRUE,        TRUE,        '+359 301 23456'),
  ('Hut Maliovitsa (Rhodopes)',         'Western Rhodopes, Shiroka Laka area',                           50,      FALSE,       3.6,         'Rhodopes',                     1250,      41.6890,         24.7340,         FALSE,       TRUE,        NULL),
  ('Hut Studenets',                     'Rhodopes, near Pamporovo ski resort',                           80,      TRUE,        4.0,         'Rhodopes',                     1530,      41.6500,         24.6800,         TRUE,        TRUE,        '+359 3021 2567'),
  ('Hut Turyan',                        'Rhodopes, Turyan locality near Smolyan',                        50,      FALSE,       3.7,         'Rhodopes',                     1380,      41.5900,         24.7200,         FALSE,       TRUE,        NULL),
  ('Hut Izgrev',                        'Rhodopes, near Pamporovo',                                      65,      TRUE,        4.0,         'Rhodopes',                     1620,      41.6380,         24.7150,         TRUE,        TRUE,        NULL),
  -- STARA PLANINA
  ('Hut Botev',                         'Central Balkan, near Botev Peak',                               90,      FALSE,       4.2,         'Stara Planina (Balkan)',        1600,      42.7159,         24.9147,         TRUE,        TRUE,        '+359 658 12345'),
  ('Hut Pleven',                        'Central Balkan National Park, Troyan Balkan',                  100,      FALSE,       4.1,         'Stara Planina (Balkan)',        1520,      42.7700,         24.7300,         TRUE,        TRUE,        '+359 670 34567'),
  ('Hut Uzana',                         'Stara Planina, Gabrovo Balkan',                                120,      TRUE,        4.4,         'Stara Planina (Balkan)',         900,      42.7830,         25.1500,         TRUE,        TRUE,        '+359 66 801234'),
  ('Hut Dermendere',                    'Stara Planina, near Kalofer',                                   70,      FALSE,       3.9,         'Stara Planina (Balkan)',        1200,      42.6600,         24.9800,         FALSE,       TRUE,        NULL),
  ('Hut Ambaritsa',                     'Central Balkan National Park, Ambaritsa ridge',                 50,      FALSE,       4.0,         'Stara Planina (Balkan)',        1676,      42.8000,         24.8500,         FALSE,       TRUE,        NULL),
  ('Hut Eho',                           'Central Balkan National Park, near Troyan Pass',                80,      FALSE,       4.3,         'Stara Planina (Balkan)',        1750,      42.7680,         24.6100,         TRUE,        TRUE,        '+359 670 56789'),
  ('Hut Rusalka',                       'Stara Planina, Shipka area',                                    60,      TRUE,        3.7,         'Stara Planina (Balkan)',        1070,      42.7330,         25.3100,         TRUE,        TRUE,        NULL),
  ('Hut Beklemeto',                     'Stara Planina, Beklemeto Pass area, Lovech',                   100,      TRUE,        4.0,         'Stara Planina (Balkan)',        1525,      42.8380,         24.5300,         TRUE,        TRUE,        '+359 69 233456'),
  -- VITOSHA
  ('Hut Aleko',                         'Vitosha Nature Park, Aleko saddle, Sofia',                     200,      TRUE,        4.5,         'Vitosha',                      1810,      42.5445,         23.2864,         TRUE,        TRUE,        '+359 2 967 2120'),
  ('Hut Cherni Vrah',                   'Vitosha Nature Park, near Cherni Vrah summit',                  40,      TRUE,        3.8,         'Vitosha',                      2264,      42.5650,         23.2720,         FALSE,       FALSE,       NULL),
  ('Hut Kumata',                        'Vitosha Nature Park, Dragalevtsi area',                         60,      TRUE,        3.9,         'Vitosha',                      1340,      42.6000,         23.3100,         TRUE,        TRUE,        '+359 88 456789'),
  ('Hut Zlatni Mostove',                'Vitosha Nature Park, Zlatni Mostove stone river',               80,      TRUE,        4.2,         'Vitosha',                      1370,      42.6150,         23.2600,         TRUE,        TRUE,        '+359 2 9623 456'),
  ('Hut Kamen Del',                     'Vitosha Nature Park, western slopes',                           30,      FALSE,       3.6,         'Vitosha',                      1360,      42.5700,         23.2400,         FALSE,       TRUE,        NULL),
  -- SREDNA GORA
  ('Hut Bogdan',                        'Sredna Gora, near Bogdan Peak',                                 50,      FALSE,       3.8,         'Sredna Gora',                  1560,      42.6000,         24.1800,         TRUE,        TRUE,        NULL),
  ('Hut Shipka',                        'Shipka Pass area, Sredna Gora',                                 80,      TRUE,        4.1,         'Sredna Gora',                  1185,      42.7480,         25.3240,         TRUE,        TRUE,        '+359 4323 2345'),
  -- OSOGOVO
  ('Hut Tsarev Vrah',                   'Osogovo mountain, near Ruen Peak',                              70,      FALSE,       3.9,         'Osogovo',                      1922,      42.1780,         22.5700,         TRUE,        TRUE,        NULL),
  ('Hut Osogovo',                       'Osogovo mountain, central section',                             90,      FALSE,       4.0,         'Osogovo',                      1560,      42.1600,         22.6100,         TRUE,        TRUE,        '+359 7815 2345'),
  -- VRACHANSKA PLANINA
  ('Hut Vrattsata',                     'Vrachanska Planina, Vratsata gorge area',                       40,      TRUE,        4.1,         'Vrachanska Planina',            650,      43.1830,         23.5620,         TRUE,        TRUE,        '+359 92 660 123'),
  ('Hut Ledenika',                      'Vrachanska Planina, near Ledenika cave',                        60,      TRUE,        3.9,         'Vrachanska Planina',            830,      43.1710,         23.5200,         TRUE,        TRUE,        NULL),
  -- STRANDZHA
  ('Hut Gramatikovo',                   'Strandzha Nature Park, Gramatikovo area',                       40,      TRUE,        3.7,         'Strandzha',                     320,      42.0600,         27.5000,         FALSE,       TRUE,        NULL),
  -- CENTRAL BALKAN
  ('Hut Tuzha',                         'Central Balkan, Tuzha locality',                                70,      FALSE,       4.0,         'Central Balkan',               1110,      42.7100,         25.0700,         TRUE,        TRUE,        NULL),
  ('Hut Mazalat',                       'Central Balkan, Karlovo area',                                  80,      FALSE,       4.1,         'Central Balkan',               1280,      42.6700,         24.8100,         TRUE,        TRUE,        '+359 335 94123'),
  -- MALYOVITSA
  ('Hut Malyovitsa (Upper)',            'Malyovitsa area, Rila National Park',                           50,      FALSE,       4.3,         'Malyovitsa',                   2129,      42.1680,         23.3630,         FALSE,       TRUE,        NULL)
) AS v(name, address, capacity, open_year_round, rating, mountain_name,
        elevation_m, latitude, longitude, has_restaurant, has_accommodation, phone)
JOIN mountain m ON m.name = v.mountain_name;

-- ============================================================
-- 4. LANDMARKS (41 entries)
-- Uses a VALUES + JOIN on mountain name to avoid hardcoded IDs
-- ============================================================

INSERT INTO landmark (name, type, latitude, longitude, description, mountain_id)
SELECT v.name, v.type, v.latitude, v.longitude, v.description, m.id
FROM (VALUES
  -- RILA
  ('Musala Peak'::text,                          'PEAK'::text,           42.1793::float8, 23.5856::float8, 'Musala (2925 m) is the highest peak in Bulgaria and on the entire Balkan Peninsula. The name means "close to God" in Arabic, possibly from Ottoman cartographers. The summit plateau hosts an automatic meteorological observatory. The ascent from Borovets via Yastrebets is the most popular route.'::text,                                                                                                                                                                            'Rila'::text),
  ('Seven Rila Lakes (Sedemte Rilski Ezera)',    'NATIONAL_PARK',        42.1933,         23.3233,         'The Seven Rila Lakes are the most visited natural landmark in Bulgaria – a cascade of seven glacial lakes in Rila National Park between 2100 and 2535 m. Each lake has a distinctive name: The Tear (Salzata), The Eye (Okoto), The Kidney (Bacheto), The Twin (Bliznaka), The Trefoil (Trilistnika), The Fish Lake (Ribnoto), and The Lower Lake (Dolnoto).',                                                                                                                        'Rila'),
  ('Malyovitsa Peak',                            'PEAK',                 42.1574,         23.3440,         'Malyovitsa (2729 m) is a dramatic rocky pyramid in western Rila. Its north face is a major destination for technical rock climbing and alpinism in Bulgaria. The hut at its base (1729 m) is the starting point for the Malyovitsa – Scary Lake – Lovnitsa circuit.',                                                                                                                                                                                                                'Rila'),
  ('Scary Lake (Strashnoto Ezero)',              'NATIONAL_PARK',        42.1800,         23.3600,         'Strashnoto Ezero (Scary Lake) is one of the most dramatic glacial lakes in Rila, located in a high cirque beneath sheer rock walls at approx. 2370 m. It is part of the Malyovitsa Lakes group and is a popular destination from Malyovitsa Hut.',                                                                                                                                                                                                                                  'Rila'),
  ('Rila Monastery',                             'MONASTERY',            42.1333,         23.3397,         'Rila Monastery (Rilski Manastir) is the largest Eastern Orthodox monastery in Bulgaria, founded in the 10th century by Saint John of Rila. It is a UNESCO World Heritage Site since 1983. Situated at 1147 m in the Rila river valley, it is one of Bulgaria''s most important cultural and historical landmarks, reachable via forest trails from Rila town.',                                                                                                                     'Rila'),
  ('Parangalitsa Reserve',                       'NATURE_RESERVE',       42.0620,         23.2740,         'Parangalitsa is a UNESCO-designated biosphere reserve within Rila National Park, preserving primeval spruce forests up to 250 years old. Accessible via the Parangalitsa Hut, the reserve is one of the last old-growth forest refugia on the Balkan Peninsula.',                                                                                                                                                                                                                   'Rila'),
  ('Musalenski Lakes',                           'NATIONAL_PARK',        42.1750,         23.5700,         'Musalenski Lakes are a group of four glacial lakes below Musala Peak at around 2150–2380 m, the highest lake complex in Bulgaria. The highest lake, Gorno Musalensko Ezero, sits at 2380 m – the highest lake in the Balkans. Ivan Vazov Hut provides base access.',                                                                                                                                                                                                               'Rila'),
  ('Skakavitsa Waterfall',                       'WATERFALL',            42.2650,         23.5700,         'Skakavitsa Waterfall (70 m) is one of the highest and most beautiful waterfalls in Bulgaria, located in the Iskar gorge area east of Borovets. It is accessible by a forest trail from the Skakavitsa Hut area.',                                                                                                                                                                                                                                                                   'Rila'),
  -- PIRIN
  ('Vihren Peak',                                'PEAK',                 41.7698,         23.4046,         'Vihren (2914 m) is the highest peak of the Pirin Mountains and the third highest in Bulgaria. The north face is a sheer 350-metre marble wall – one of the most challenging climbing venues in Bulgaria. The standard route from Vihren Hut takes approximately 2.5 hours.',                                                                                                                                                                                                        'Pirin'),
  ('Kutelo Peak',                                'PEAK',                 41.7588,         23.3989,         'Kutelo (2908 m) is the second highest peak of Pirin, a sharp pyramidal summit connected to Vihren by a narrow rocky ridge. The traverse of the Vihren–Kutelo–Banski Suhodol ridge is a classic Pirin mountaineering objective.',                                                                                                                                                                                                                                                    'Pirin'),
  ('Popovo Lake',                                'NATIONAL_PARK',        41.7325,         23.4562,         'Popovo Lake (Popovo Ezero) is the largest glacial lake in Pirin at 2234 m, covering 12.4 ha. It lies in a spectacular cirque below Kamenitsa and Kozi Vrah peaks. The route from Bezbog Hut to Popovo Lake traverses some of the finest alpine scenery in the Balkans.',                                                                                                                                                                                                          'Pirin'),
  ('Bezbog Lake',                                'NATIONAL_PARK',        41.6798,         23.3201,         'Bezbog Lake is a glacial lake at 2236 m accessible by cabin lift from Dobrinishte. The Bezbog Hut beside it is a base for routes into the northern Pirin Lakes region (Popovo, Tevno, Ribno groups).',                                                                                                                                                                                                                                                                              'Pirin'),
  ('Koncheto Ridge',                             'CREST',                41.7650,         23.4110,         'Koncheto (The Little Horse) is a spectacular rocky ridge below Vihren connecting the hut area with the southern Pirin. The narrow crest with steel cables is one of the most exhilarating via ferrata–style hikes in Bulgaria, requiring a good head for heights.',                                                                                                                                                                                                                 'Pirin'),
  ('Bansko Old Town',                            'MONASTERY',            41.8321,         23.4864,         'Bansko is a UNESCO-designated historical town at the foot of Pirin. Its cobblestone streets, fortified merchant houses (kuli), and the Holy Trinity Church make it the main cultural access point for the Pirin mountains. The town is also Bulgaria''s premier ski resort.',                                                                                                                                                                                                         'Pirin'),
  ('Popina Laka',                                'NATIONAL_PARK',        41.8200,         23.5300,         'Popina Laka is a valley north of Bansko in Pirin, popular for family hiking and mountain biking. The area contains old-growth Bosnian pine forests and connects to the Bayuvi Dupki – Dzhindzhiritsa Biosphere Reserve, one of the oldest strict reserves in Bulgaria.',                                                                                                                                                                                                            'Pirin'),
  ('Bayuvi Dupki Reserve',                       'NATURE_RESERVE',       41.7200,         23.5600,         'Bayuvi Dupki – Dzhindzhiritsa is a UNESCO biosphere reserve in Pirin protecting the oldest Bosnian pine trees in the world (some over 1300 years old). The reserve was established in 1934 as one of Bulgaria''s first strict nature reserves.',                                                                                                                                                                                                                                    'Pirin'),
  -- RHODOPES
  ('Trigrad Gorge',                              'CANYON',               41.5460,         24.4080,         'Trigrad Gorge is one of the most spectacular canyon systems in the Rhodopes, carved by the Trigrad River through 300-metre-high vertical marble walls. At its heart lies the Devil''s Throat Cave (Dyavolskoto Garlo), where a river plunges 42 m underground into a pitch-black cavern.',                                                                                                                                                                                          'Rhodopes'),
  ('Devil''s Throat Cave (Dyavolskoto Garlo)',   'CAVE',                 41.5430,         24.4150,         'Devil''s Throat Cave is the most dramatic cave in Bulgaria. The Trigrad River plunges 42 m through a 30-metre-wide hole, creating one of Europe''s most powerful underground waterfalls. The cave was used as a filming location and is a major ecotourism site. Its exit portal is separate from its entry.',                                                                                                                                                                       'Rhodopes'),
  ('Buynovsko Gorge',                            'CANYON',               41.5700,         24.5600,         'Buynovsko Gorge (Buynovo Gorge) on the Buynovska River is one of the narrowest and longest gorges in Bulgaria, with walls almost touching at the top in places. The 4 km eco-trail through the gorge is one of the most popular day hikes in the Rhodopes.',                                                                                                                                                                                                                        'Rhodopes'),
  ('Yagodinska Cave',                            'CAVE',                 41.5330,         24.3880,         'Yagodinska Cave is one of the longest caves in Bulgaria (nearly 10 km of mapped passages), situated near Yagodina village. It contains remarkable stalactite and stalagmite formations and prehistoric pottery and human bones have been discovered inside.',                                                                                                                                                                                                                        'Rhodopes'),
  ('Shiroka Laka Village',                       'ROCK_FORMATION',       41.7010,         24.6270,         'Shiroka Laka is a protected architectural and ethnographic reserve village in the western Rhodopes, known for its traditional Bulgarian Revival-period stone houses perched on a hillside above the Shiroka Laka River. It is a base for hiking to Golyam Perelik (2191 m).',                                                                                                                                                                                                         'Rhodopes'),
  ('Golyam Perelik Peak',                        'PEAK',                 41.6053,         24.5586,         'Golyam Perelik (2191 m) is the highest peak of the Rhodope Mountains, situated in the central Rhodopes south of Smolyan. The summit has a telecommunications tower and offers panoramic views over the entire range. The hike from Prespa village takes about 4 hours.',                                                                                                                                                                                                             'Rhodopes'),
  ('Aglika Lake (Shiroka Polyana)',              'NATIONAL_PARK',        41.7600,         24.4800,         'Shiroka Polyana reservoir (also known as the Aglika Lake area) is an artificial lake in the western Rhodopes used for water supply. The surrounding forests and meadows make it a popular recreation area with trails connecting to Batak and Tsigov Chark.',                                                                                                                                                                                                                         'Rhodopes'),
  ('Persenk Peak',                               'PEAK',                 41.7630,         24.5650,         'Persenk (1935 m) is the highest peak of the Eastern Rhodopes and the second highest of the entire Rhodope range. It is reached from Persenk Hut via a 2-hour trail through beech forests.',                                                                                                                                                                                                                                                                                        'Rhodopes'),
  ('Bachkovo Monastery',                         'MONASTERY',            41.9433,         24.8580,         'Bachkovo Monastery (Bachkovski Manastir) is the second largest monastery in Bulgaria after Rila, founded in 1083 by the Georgian military commander Grigorios Bakuriani. Situated in the Rhodopes near Asenovgrad, it is accessible by a scenic gorge trail along the Asenitsa River.',                                                                                                                                                                                             'Rhodopes'),
  ('Asen Fortress',                              'ROCK_FORMATION',       41.9500,         24.8700,         'Asen Fortress (Asenova Krepost) is a medieval Bulgarian fortress in the Asenovgrad gorge perched on a rocky cliff above the Asenitsa River. The ruins include the beautifully preserved Church of the Holy Mother of God (12th century). The site offers dramatic views of the surrounding Rhodope foothills.',                                                                                                                                                                      'Rhodopes'),
  -- STARA PLANINA
  ('Botev Peak',                                 'PEAK',                 42.7190,         24.9147,         'Botev (2376 m) is the highest peak of Stara Planina (Balkan Range) and of the Central Balkan National Park. Named after Bulgarian poet-revolutionary Hristo Botev, it was previously called Yumrukchal. The Botev Waterfall descends from the massif''s north face.',                                                                                                                                                                                                               'Stara Planina (Balkan)'),
  ('Boatin Reserve',                             'NATURE_RESERVE',       42.7700,         24.8000,         'Boatin is a UNESCO-designated biosphere reserve and strict nature reserve within the Central Balkan National Park, preserving old-growth beech and spruce forests along the Boatin river valley. Access is restricted to scientific research; the surrounding zone is open for hiking.',                                                                                                                                                                                              'Stara Planina (Balkan)'),
  ('Raysko Praskalo Waterfall',                  'WATERFALL',            42.8128,         24.8720,         'Raysko Praskalo (326 m) is the highest waterfall in Bulgaria and one of the highest in the Balkans. Located in the Central Balkan National Park above Kalofer, it can be reached via a 5-hour trail from the village. It is most powerful in spring snowmelt season.',                                                                                                                                                                                                              'Stara Planina (Balkan)'),
  ('Shipka Pass',                                'PEAK',                 42.7480,         25.3234,         'Shipka Pass (1185 m) is a mountain pass through Stara Planina that was the site of decisive battles during the Russo-Turkish War of 1877–1878. The Shipka Memorial (Freedom Monument) stands at the summit. The pass is a major historical landmark visited by hundreds of thousands annually.',                                                                                                                                                                                    'Stara Planina (Balkan)'),
  ('Kozya Stena Reserve',                        'NATURE_RESERVE',       42.6800,         24.8000,         'Kozya Stena (Goat Wall) is a strict nature reserve in the Central Balkan National Park, named for the sheer limestone cliffs where chamois (wild mountain goats) live. It is one of the last habitats in Bulgaria for the chamois.',                                                                                                                                                                                                                                                'Stara Planina (Balkan)'),
  ('Buzludzha Monument',                         'ROCK_FORMATION',       42.7353,         25.3945,         'Buzludzha is a futurist concrete monument built in 1981 atop the Buzludzha Peak (1432 m) in Stara Planina, now abandoned and visually striking against the skyline. The peak itself was the site of a congress of Bulgarian socialists in 1891. It is a popular photographic destination.',                                                                                                                                                                                         'Stara Planina (Balkan)'),
  -- VITOSHA
  ('Cherni Vrah Summit',                         'PEAK',                 42.5650,         23.2720,         'Cherni Vrah (2290 m) is the highest peak of Vitosha and the most climbed summit in Bulgaria due to its proximity to Sofia. The summit has a meteorological station and a small shelter. On clear days Sofia, the Rila, and Rhodope mountains are visible. The standard ascent from Aleko Hut takes 1 hour.',                                                                                                                                                                        'Vitosha'),
  ('Stone Rivers (Moreni)',                      'ROCK_FORMATION',       42.5900,         23.2800,         'Vitosha''s famous stone rivers (moreni) are periglacial boulder fields formed during the last ice age by freeze-thaw cycles. Some flow continuously downhill at imperceptibly slow rates. The largest stone river near Zlatni Mostove is 2 km long and up to 100 m wide.',                                                                                                                                                                                                            'Vitosha'),
  ('Boyana Church',                              'MONASTERY',            42.6458,         23.2641,         'Boyana Church is a medieval Bulgarian Orthodox church at the foot of Vitosha, listed as a UNESCO World Heritage Site. It contains unique 13th-century frescoes that are considered among the finest examples of medieval European painting. Situated at 840 m, it is accessible from the Dragalevtsi neighbourhood of Sofia.',                                                                                                                                                        'Vitosha'),
  -- VRACHANSKA PLANINA
  ('Vratsata Gorge',                             'CANYON',               43.1818,         23.5617,         'Vratsata is one of the longest and most spectacular rock gorges in the Balkans, stretching 23 km with walls up to 700 m high. It is situated within the Vrachanska Planina Nature Park near Vratsa. The gorge is a world-class rock climbing destination with over 600 established routes.',                                                                                                                                                                                        'Vrachanska Planina'),
  ('Ledenika Cave',                              'CAVE',                 43.1680,         23.5150,         'Ledenika Cave is a show cave in the foothills of Vrachanska Planina, 16 km west of Vratsa. It is known for its stunning stalactite and stalagmite formations and for the ice formations that develop in its entrance hall in winter. The cave is approximately 300 m long and has been open to tourists since 1961.',                                                                                                                                                                'Vrachanska Planina'),
  -- OSOGOVO
  ('Ruen Peak',                                  'PEAK',                 42.1758,         22.5710,         'Ruen (2252 m) is the highest peak of the Osogovo Mountains and one of the highest summits in western Bulgaria. The summit straddles the Bulgarian-North Macedonian border and offers panoramic views over both countries.',                                                                                                                                                                                                                                                          'Osogovo'),
  -- STRANDZHA
  ('Strandzha Nature Park',                      'NATURE_RESERVE',       42.1000,         27.5500,         'Strandzha Nature Park (1161 km²) is the largest nature park in Bulgaria, covering the mountains and Black Sea coast of southeast Bulgaria. It protects ancient Euxine-Colchic broadleaf forests with endemic Pontian species of rhododendron, cherry laurel, and holly.',                                                                                                                                                                                                            'Strandzha'),
  -- CENTRAL BALKAN
  ('Dzhendema Reserve',                          'NATURE_RESERVE',       42.7500,         24.9800,         'Dzhendema (1058 ha) is the largest strict nature reserve in Bulgaria, entirely within the Central Balkan National Park. It preserves old-growth beech, spruce, and fir forests with fallen timber left untouched, creating critical habitat for woodpeckers, owls, and large predators.',                                                                                                                                                                                            'Central Balkan'),
  ('Steneto Reserve',                            'NATURE_RESERVE',       42.8380,         24.5800,         'Steneto is a strict nature reserve in the western section of the Central Balkan National Park near Teteven. It protects one of the last primeval beech forests in Bulgaria. The deep canyon of the Beli Osam river passes through the reserve.',                                                                                                                                                                                                                                     'Central Balkan')
) AS v(name, type, latitude, longitude, description, mountain_name)
JOIN mountain m ON m.name = v.mountain_name;

-- ============================================================
-- 5. HIKING ROUTES (22 entries)
-- Uses subqueries to safely reference mountain IDs by name
-- ============================================================

-- RILA
INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Musala from Borovets (via Yastrebets)', 14.0, 360, 'HARD',
'The classic route to the highest peak in Bulgaria (2925 m). Take the Yastrebets gondola to 2369 m, then follow the red-marked trail over the Musalenski Lakes saddle to the summit. Stunning 360° panorama on clear days; snow may persist until June.',
id FROM mountain WHERE name = 'Rila';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Seven Rila Lakes Circuit', 8.0, 240, 'MEDIUM',
'The most popular hike in Bulgaria. Starting from Seven Lakes Hut (2198 m) the loop visits all seven glacial lakes – The Tear, The Eye, The Kidney, The Twin, The Trefoil, The Fish Lake, and The Lower Lake – descending and re-ascending between each. Stunning alpine scenery throughout.',
id FROM mountain WHERE name = 'Rila';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Malyovitsa Peak from Malyovitsa Hut', 9.0, 270, 'MEDIUM',
'A rewarding ascent of the iconic Malyovitsa (2729 m) starting from the hut at 1729 m. The trail climbs through dwarf pine, passes Scary Lake (Strashnoto Ezero), and reaches the summit ridge. The panorama of western Rila is exceptional.',
id FROM mountain WHERE name = 'Rila';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Rila Monastery to Skakavitsa Waterfall', 12.0, 300, 'MEDIUM',
'Starting from Rila Monastery (1147 m), the trail follows the Rilska River upstream before climbing through centuries-old spruce forest to the 70-metre Skakavitsa Waterfall. A beautiful combination of cultural heritage and nature.',
id FROM mountain WHERE name = 'Rila';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Musalenski Lakes from Ivan Vazov Hut', 6.0, 180, 'MEDIUM',
'A circuit of the four highest lakes in the Balkans, starting from Ivan Vazov Hut (2369 m). The uppermost lake, Gorno Musalensko Ezero at 2380 m, is the highest lake in the Balkans. Best visited July–September.',
id FROM mountain WHERE name = 'Rila';

-- PIRIN
INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Vihren Peak from Vihren Hut', 6.0, 180, 'HARD',
'The standard ascent of Vihren (2914 m), the highest marble peak in Bulgaria. From Vihren Hut (1950 m) the red-marked trail climbs steeply through dwarf pine to the rocky summit. The view takes in the entire Pirin range, Rila, and Macedonia on clear days.',
id FROM mountain WHERE name = 'Pirin';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Koncheto Ridge Traverse (Vihren – Kutelo)', 5.0, 240, 'HARD',
'One of the most thrilling hikes in Bulgaria: a narrow rocky ridge connecting Vihren (2914 m) and Kutelo (2908 m) secured with steel cables. Requires no climbing gear but a head for heights is essential. Not suitable in wet or icy conditions.',
id FROM mountain WHERE name = 'Pirin';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Bezbog Lakes Circuit from Dobrinishte', 18.0, 360, 'MEDIUM',
'Take the Bezbog cabin lift from Dobrinishte to 2236 m then hike the circuit of Bezbog, Popovo, and the northern lake group. One of the finest alpine lake panoramas in the Balkans. Return by lift or on foot.',
id FROM mountain WHERE name = 'Pirin';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Banderitsa Valley to Vihren Hut', 10.0, 240, 'MEDIUM',
'The classic approach to northern Pirin from Bansko. The trail follows the Banderitsa river through an old-growth Bosnian pine forest – home to bears and roe deer – before climbing to Banderitsa Hut (1810 m) and then Vihren Hut (1950 m).',
id FROM mountain WHERE name = 'Pirin';

-- VITOSHA
INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Cherni Vrah from Dragalevtsi', 8.0, 180, 'MEDIUM',
'The most popular Vitosha route from Sofia: starting at Dragalevtsi monastery (700 m), the trail climbs through beech and pine to Aleko Hut (1810 m) and on to Cherni Vrah (2290 m). Magnificent city views on the descent.',
id FROM mountain WHERE name = 'Vitosha';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Cherni Vrah from Aleko Hut', 4.0, 90, 'EASY',
'A short and easy summit walk from Aleko Hut across Vitosha''s famous stone river moraines to Cherni Vrah (2290 m). Ideal for families and first-time mountain visitors. The gondola from Simeonovo can be used for the ascent.',
id FROM mountain WHERE name = 'Vitosha';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Zlatni Mostove Stone Rivers', 3.0, 60, 'EASY',
'A gentle walk through Vitosha''s Zlatni Mostove (Golden Bridges) area to view the largest periglacial stone river in Bulgaria. The boulder field is 2 km long and up to 100 m wide. Very popular for picnics and family walks year-round.',
id FROM mountain WHERE name = 'Vitosha';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Boyana Church to Zlatni Mostove', 7.0, 150, 'EASY',
'Links Sofia''s two UNESCO-listed treasures: starting at Boyana Church (840 m), the trail climbs through Vitosha''s oak and beech belt to the stone rivers at Zlatni Mostove. An ideal half-day outing from the city.',
id FROM mountain WHERE name = 'Vitosha';

-- STARA PLANINA
INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Botev Peak from Kalofer', 20.0, 480, 'HARD',
'The classic ascent of Botev (2376 m) from the historic town of Kalofer. The route passes through the Central Balkan National Park, the Raysko Praskalo waterfall (326 m – highest in Bulgaria), and the Botev Hut before the final push to the summit. A full-day undertaking for fit hikers.',
id FROM mountain WHERE name = 'Stara Planina (Balkan)';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Raysko Praskalo Waterfall from Kalofer', 14.0, 300, 'MEDIUM',
'Ascend through the Central Balkan National Park to see Raysko Praskalo (326 m), the highest waterfall in Bulgaria. The trail from Kalofer follows the Tundzha River through magnificent old-growth beech forests. Return on the same trail.',
id FROM mountain WHERE name = 'Stara Planina (Balkan)';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Shipka Pass – Freedom Monument Circuit', 5.0, 120, 'EASY',
'Walk from Shipka village up to the Shipka Pass (1185 m) and the iconic Freedom Monument commemorating the 1877–78 Russo-Turkish War battles. The summit offers a panorama across both the Danube Plain and the Thracian valley.',
id FROM mountain WHERE name = 'Stara Planina (Balkan)';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Vratsata Gorge Eco-Trail', 8.0, 180, 'MEDIUM',
'A spectacular trail through the Vratsata gorge near Vratsa, with 700 m vertical rock walls towering overhead. The route passes beneath world-class rock climbing faces and through pine woodland before emerging above the gorge for sweeping views.',
id FROM mountain WHERE name = 'Vrachanska Planina';

-- RHODOPES
INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Devil''s Throat Cave and Trigrad Gorge', 7.0, 180, 'MEDIUM',
'Explore the dramatic Trigrad Gorge and its centrepiece, Devil''s Throat Cave (Dyavolskoto Garlo), where the Trigrad River plunges 42 m into darkness. The route follows the gorge road trail with stunning marble cliff scenery.',
id FROM mountain WHERE name = 'Rhodopes';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Buynovsko Gorge Eco-Trail', 4.0, 90, 'EASY',
'Walk through one of the narrowest gorges in Bulgaria – Buynovsko Gorge – along a well-maintained boardwalk trail. The gorge walls nearly touch at the top in places. Perfectly accessible for all ages and abilities.',
id FROM mountain WHERE name = 'Rhodopes';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Golyam Perelik Peak from Shiroka Laka', 16.0, 360, 'HARD',
'The ascent to the highest Rhodope summit (2191 m) from the ethnographic village of Shiroka Laka. The trail climbs through mixed forest and subalpine meadows. The summit offers a view of over 100 km across the range on clear days.',
id FROM mountain WHERE name = 'Rhodopes';

INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Bachkovo Monastery Gorge Trail', 6.0, 120, 'EASY',
'Follow the scenic gorge of the Asenitsa River from Asenovgrad to Bachkovo Monastery and onwards to Asen Fortress. The trail passes vineyards, a medieval bridge, and dramatic Rhodope foothills scenery. Suitable for all fitness levels.',
id FROM mountain WHERE name = 'Rhodopes';

-- OSOGOVO
INSERT INTO hiking_route (name, distance_km, duration_min, difficulty, description, mountain_id)
SELECT 'Ruen Peak from Kyustendil', 14.0, 360, 'HARD',
'Ascent of Ruen (2252 m), the highest summit of Osogovo, from the village of Rebro near Kyustendil. The route climbs through dense coniferous forest before breaking out onto the open rocky summit ridge with views into both Bulgaria and North Macedonia.',
id FROM mountain WHERE name = 'Osogovo';

-- ============================================================
-- 6. SEED CHALLENGES
-- ============================================================

INSERT INTO challenge (name, description, type, target_count) VALUES
('Peak Bagger – Rila',         'Climb all five peaks above 2800 m in Rila: Musala, Malyovitsa, Kupena, Orlovets, and Elenin Vrah.', 'DISTANCE', 5),
('Pirin High-Route',           'Complete the classic three-day traverse of Pirin from Vihren Hut to Bezbog Hut, passing Kutelo, Polezhan, Todorka, and Kamenitsa.', 'HIKE_COUNT', 3),
('Seven Lakes Pilgrim',        'Visit the Seven Rila Lakes three times in one season – spring, summer, and autumn – to experience each in a different mood.', 'HIKE_COUNT', 3),
('Stara Planina end-to-end',   'Complete the 650 km Kom–Emine long-distance trail across the entire length of the Balkan Range.', 'DISTANCE', 650),
('Vitosha 10×',                'Climb Cherni Vrah ten times within a single calendar year.', 'HIKE_COUNT', 10),
('Hut Hopper',                 'Spend at least one night in 10 different Bulgarian mountain huts.', 'HIKE_COUNT', 10),
('Waterfall Hunter',           'Visit five major waterfalls: Raysko Praskalo, Skakavitsa, Boyana, Suchurum, and Kozi Skoak.', 'HIKE_COUNT', 5),
('Cave Explorer',              'Explore three show caves in Bulgaria: Ledenika, Yagodinska, and Devil''s Throat.', 'HIKE_COUNT', 3);

-- ============================================================
-- 7. SEED EVENTS
-- ============================================================

INSERT INTO event (title, description, start_time, end_time, location) VALUES
('Spring Opening of Seven Lakes', 'Community hike to celebrate the opening of the Seven Lakes Hut for the summer season. Join fellow hikers for a group ascent and a shared meal at the hut. All fitness levels welcome.', '2026-06-07 09:00:00', '2026-06-07 18:00:00', 'Seven Rila Lakes Hut, Rila'),
('Midsummer Musala Night Ascent', 'A memorable night hike to reach the summit of Musala at dawn. Depart Borovets at 02:00 to witness the sunrise from 2925 m. Headlamps and warm layers required.', '2026-06-21 02:00:00', '2026-06-21 10:00:00', 'Borovets, Rila'),
('Vitosha Autumn Colours Walk', 'A guided walk through Vitosha''s beech forests during peak autumn foliage, ending at Aleko Hut for hot tea and soup. Suitable for beginners and families.', '2026-10-11 10:00:00', '2026-10-11 16:00:00', 'Dragalevtsi, Vitosha'),
('Pirin Ridge Traverse – Group Expedition', 'A three-day guided group traverse of the Pirin high route: Vihren Hut → Bezbog → Popovo Lake → Yavorov Hut. Accommodation in mountain huts. Experience level: intermediate.', '2026-07-18 08:00:00', '2026-07-20 18:00:00', 'Bansko, Pirin'),
('Stara Planina Trail Cleanup', 'Volunteer day on the Central Balkan trail network. Help maintain the path from Uzana to Ambaritsa Hut by clearing overgrown sections and repainting waymarkers. Gloves and tools provided.', '2026-05-23 08:30:00', '2026-05-23 16:00:00', 'Uzana Hut, Stara Planina'),
('Rhodopes Full Moon Trek', 'An atmospheric evening hike through the Buynovsko Gorge by moonlight, finishing at Yagodinska Cave for a torchlit visit. Limited to 20 participants.', '2026-08-19 20:00:00', '2026-08-20 01:00:00', 'Buynovsko Gorge, Rhodopes'),
('Bachkovo Pilgrimage Hike', 'A traditional hiking pilgrimage from Asenovgrad along the gorge trail to Bachkovo Monastery, one of Bulgaria''s holiest sites. The route follows a path walked by pilgrims for over 900 years.', '2026-09-05 07:00:00', '2026-09-05 14:00:00', 'Asenovgrad, Rhodopes'),
('Rila National Park Bioblitz', 'A 24-hour citizen-science event in Rila National Park. Participants record as many species of plants, birds, insects, and mammals as possible using the iNaturalist app. Join a team or go solo.', '2026-07-04 08:00:00', '2026-07-05 08:00:00', 'Parangalitsa Reserve, Rila');

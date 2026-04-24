package com.example.cosmicexplorer;

import java.util.HashMap;
import java.util.Map;

public class StarInfoData {

    public static class Info {
        public String constellation;
        public String distanceLy;
        public String colorClass;
        public String mythology;
        public boolean isPlanet;

        public Info(String constellation, String distanceLy,
                    String colorClass, String mythology) {
            this.constellation = constellation;
            this.distanceLy    = distanceLy;
            this.colorClass    = colorClass;
            this.mythology     = mythology;
            this.isPlanet      = false;
        }
    }

    private static final Map<String, Info> data = new HashMap<>();

    static {
        data.put("Sirius",     new Info("Canis Major", "8.6 ly",  "Blue-White A1",
                "The brightest star in Earth's night sky. Ancient Egyptians called it Sopdet and used its heliacal rising to predict the annual Nile flood."));
        data.put("Canopus",    new Info("Carina",       "310 ly",  "Yellow-White F0",
                "Second brightest star. Used as a navigation reference by spacecraft. Named after the helmsman of Menelaus in Greek legend."));
        data.put("Arcturus",   new Info("Boötes",       "37 ly",   "Orange K1",
                "Brightest star in the northern hemisphere. In Greek myth, Arcturus is associated with Boötes the Herdsman, guarding the Great Bear."));
        data.put("Vega",       new Info("Lyra",         "25 ly",   "Blue-White A0",
                "Once and future North Star due to precession. In Japanese legend, Vega is Orihime, the weaver princess separated from her love Altair by the Milky Way."));
        data.put("Capella",    new Info("Auriga",        "43 ly",   "Yellow G/G",
                "A binary star system of two golden giant stars orbiting each other. In myth, represents the she-goat Amalthea who nursed the infant Zeus."));
        data.put("Rigel",      new Info("Orion",        "860 ly",  "Blue-White B8",
                "A blue supergiant marking Orion's left foot. It shines 120,000 times more luminously than our Sun. In Arabic, Rigel means 'the left leg of the giant'."));
        data.put("Betelgeuse", new Info("Orion",        "700 ly",  "Red M2",
                "A red supergiant destined to explode as a supernova. Already shows signs of dimming. Its name derives from Arabic for 'the armpit of the giant'."));
        data.put("Altair",     new Info("Aquila",       "17 ly",   "White A7",
                "One of the closest naked-eye stars. Rotates so fast it bulges at its equator. In Japanese legend, Altair is Hikoboshi, the cowherd prince."));
        data.put("Aldebaran",  new Info("Taurus",       "65 ly",   "Orange K5",
                "The red eye of Taurus the Bull. The word means 'the follower' in Arabic, as it follows the Pleiades across the sky."));
        data.put("Antares",    new Info("Scorpius",     "550 ly",  "Red M1",
                "Rival of Mars — that is what Antares means. A red supergiant so large it would extend to Jupiter's orbit if centered on our Sun."));
        data.put("Spica",      new Info("Virgo",        "250 ly",  "Blue-White B1",
                "A binary star and one of the brightest in the sky. Hipparchus used Spica to discover the precession of Earth's axis around 130 BC."));
        data.put("Deneb",      new Info("Cygnus",       "~2600 ly","White A2",
                "One of the most luminous stars known — it could be 200,000 times brighter than the Sun. Forms the tail of Cygnus the Swan."));
        data.put("Regulus",    new Info("Leo",          "79 ly",   "Blue-White B7",
                "Heart of the Lion. Lies almost exactly on the ecliptic, so it is regularly occulted by the Moon. Its name means 'little king' in Latin."));
        data.put("Sirius",     new Info("Canis Major",  "8.6 ly",  "Blue-White A1",
                "The Dog Star. So bright it was believed to add to summer heat — giving us the phrase 'dog days of summer'."));
        data.put("Pollux",     new Info("Gemini",       "34 ly",   "Orange K0",
                "The brighter of the Gemini twins. A confirmed exoplanet orbits Pollux. In myth, Pollux was the immortal son of Zeus."));
        data.put("Fomalhaut",  new Info("Piscis Austrinus","25 ly","White A3",
                "The Loneliest Bright Star — it sits in an otherwise faint region. Has a dusty debris disk, making it a candidate for hosting planets."));

        // Planets
        Info mars = new Info("—", "~225M km", "Red planet",
                "The red color comes from iron oxide. Mars has the largest volcano in the solar system — Olympus Mons — nearly 3 times the height of Everest.");
        mars.isPlanet = true;
        data.put("Mars", mars);

        Info jupiter = new Info("—", "~630M km", "Gas giant",
                "The largest planet in our solar system. Its Great Red Spot is a storm that has raged for at least 350 years. Has 95 known moons.");
        jupiter.isPlanet = true;
        data.put("Jupiter", jupiter);

        Info saturn = new Info("—", "~1.2B km", "Ringed giant",
                "Saturn's iconic rings are made of ice and rock. The rings span 282,000 km but are only about 10–100 metres thick.");
        saturn.isPlanet = true;
        data.put("Saturn", saturn);

        Info venus = new Info("—", "~170M km", "Brightest planet",
                "The hottest planet at 465°C — hotter than Mercury despite being further from the Sun. Rotates backwards compared to most planets.");
        venus.isPlanet = true;
        data.put("Venus", venus);

        Info mercury = new Info("—", "~155M km", "Innermost",
                "The smallest planet and fastest orbiter — one Mercury year is just 88 Earth days. Has no atmosphere and extreme temperature swings.");
        mercury.isPlanet = true;
        data.put("Mercury", mercury);
    }

    public static Info get(String starName) {
        return data.get(starName);
    }

    public static String getColorName(int argbColor) {
        int r = (argbColor >> 16) & 0xFF;
        int b =  argbColor        & 0xFF;
        if (b > r + 30) return "Blue-white";
        if (r > b + 60) return "Red/Orange";
        if (r > b + 20) return "Yellow";
        return "White";
    }
}

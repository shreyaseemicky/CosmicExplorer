package com.example.cosmicexplorer;

import java.util.Arrays;
import java.util.List;

public class SkyTour {

    public static class Tour {
        public String title;
        public String emoji;
        public String description;
        public String funFact;
        public String bestTime;
        public String visibility;
        public List<String> highlightStars;

        public Tour(String emoji, String title, String description,
                    String funFact, String bestTime, String visibility,
                    String... stars) {
            this.emoji          = emoji;
            this.title          = title;
            this.description    = description;
            this.funFact        = funFact;
            this.bestTime       = bestTime;
            this.visibility     = visibility;
            this.highlightStars = Arrays.asList(stars);
        }
    }

    public static Tour[] getAllTours() {
        return new Tour[]{

                new Tour("🪐", "The Planets",
                        "Explore the five naked-eye planets visible in tonight's sky. Unlike stars, planets shine with a steady light — they don't twinkle because they appear as tiny discs rather than point sources.",
                        "Venus is so bright it can cast a shadow on a dark night. Jupiter's four largest moons were discovered by Galileo in 1610 using a simple telescope.",
                        "Shortly after sunset / before sunrise",
                        "Naked eye — all sky",
                        "Mars", "Jupiter", "Saturn", "Venus", "Mercury"),

                new Tour("☀️", "Summer Constellations",
                        "The summer sky is dominated by the Summer Triangle — an asterism formed by Vega (in Lyra), Deneb (in Cygnus) and Altair (in Aquila). Scorpius and Sagittarius hug the southern horizon near the Milky Way core.",
                        "The center of our Milky Way galaxy lies in the direction of Sagittarius, about 26,000 light-years away. On a dark night you can see the galaxy's dusty core as a bright band.",
                        "June – September, 10 PM local time",
                        "Best from both hemispheres",
                        "Vega", "Deneb", "Altair", "Arcturus", "Antares"),

                new Tour("❄️", "Winter Constellations",
                        "Winter brings the most brilliant region of sky — Orion the Hunter stands tall with his belt of three stars. Around him cluster Taurus, Gemini, Canis Major and Canis Minor, forming the Winter Hexagon of bright stars.",
                        "Betelgeuse in Orion's shoulder is a red supergiant so large that if placed at our Sun's position, it would extend past Jupiter's orbit. It is expected to explode as a supernova sometime in the next 100,000 years.",
                        "December – February, 9 PM local time",
                        "Best from northern hemisphere",
                        "Betelgeuse", "Rigel", "Sirius", "Aldebaran",
                        "Capella", "Procyon", "Pollux", "Castor"),

                new Tour("🇧🇾", "Belarus Under the Stars",
                        "From Belarus at latitude 52°N, the sky offers a rich view of both northern and mid-latitude constellations. Ursa Major (the Great Bear) is circumpolar — it never sets and wheels around Polaris all year long.",
                        "The Mir Castle and Nesvizh Castle in Belarus are UNESCO heritage sites and spectacular spots for stargazing away from city lights. Belarus has some of the darkest skies in central Europe.",
                        "Year-round; best in July – August",
                        "Northern hemisphere, 52°N",
                        "Capella", "Vega", "Deneb", "Altair", "Arcturus"),
        };
    }
}

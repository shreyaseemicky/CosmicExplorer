package com.example.cosmicexplorer;

public class SkyCulture {

    public enum Culture {
        WESTERN, ASIAN, BUGIS, CHINESE, INDIAN_VEDIC, JAPANESE, KOREAN
    }

    public static class CultureData {
        public String name;
        public String emoji;
        public String description;
        public String lore;
        public Culture culture;

        public CultureData(Culture culture, String emoji,
                           String name, String description, String lore) {
            this.culture     = culture;
            this.emoji       = emoji;
            this.name        = name;
            this.description = description;
            this.lore        = lore;
        }
    }

    public static CultureData[] getAllCultures() {
        return new CultureData[]{
                new CultureData(Culture.WESTERN, "⭐", "Western",
                        "The modern IAU 88 constellations used worldwide, rooted in ancient Greek and Roman astronomy.",
                        "Greek mythology gave us Orion the Hunter, Perseus the Hero, and Cassiopeia the Queen. The Romans inherited and renamed many of these patterns, passing them into the modern scientific sky."),

                new CultureData(Culture.ASIAN, "🌏", "Asian",
                        "Pan-Asian sky lore blending Chinese, Indian and Southeast Asian traditions into a unified celestial view.",
                        "Asian sky traditions see the heavens as a celestial empire, with the North Star as the emperor and surrounding stars as court officials and servants."),

                new CultureData(Culture.BUGIS, "⚓", "Bugis",
                        "Navigation star lore of the Bugis seafarers of Sulawesi, Indonesia — master sailors of the ancient world.",
                        "The Bugis used stars like Bintang Tujuh (the Pleiades) and Bintang Pari (the Southern Cross) to navigate the vast seas of Southeast Asia without instruments."),

                new CultureData(Culture.CHINESE, "🐉", "Chinese",
                        "Ancient Chinese sky divided into 28 lunar mansions (Xiù) and 3 enclosures — a celestial bureaucracy mirroring the imperial court.",
                        "The 28 lunar mansions track the Moon's monthly journey. The Azure Dragon rules the east, the White Tiger the west, the Black Tortoise the north, and the Vermilion Bird the south."),

                new CultureData(Culture.INDIAN_VEDIC, "🕉", "Indian Vedic",
                        "The Vedic nakshatra system divides the sky into 27 lunar mansions used in Jyotisha (Vedic astrology) for over 3,000 years.",
                        "Each nakshatra has a presiding deity, a ruling planet, and symbolic meaning. Abhijit (near Vega) was once a nakshatra but was dropped from the system, though it still holds sacred significance."),

                new CultureData(Culture.JAPANESE, "⛩", "Japanese Lunar",
                        "Japanese lunar stations (Nijūhasshuku) are the 28 lunar mansions adapted from Chinese astronomy, given Japanese names and folklore.",
                        "Japanese star lore connects the heavens to the seasons, agriculture and poetry. The Tanabata festival celebrates Orihime (Vega) and Kengyuu (Altair) separated by the Milky Way."),

                new CultureData(Culture.KOREAN, "🌙", "Korean",
                        "Korean star lore based on the Cheonsang Yeolcha Bunya Jido — a 14th-century star map engraved on stone, showing 1,467 stars.",
                        "Korean astronomy blended Chinese traditions with indigenous sky knowledge. Stars were used to predict agricultural seasons and interpret omens for the royal court."),
        };
    }

    public static String[] getConstellationNames(Culture culture) {
        switch (culture) {
            case CHINESE:
                return new String[]{"参宿 Shēn (Orion)", "织女 Zhī Nǚ (Vega)", "牛郎 Niú Láng (Altair)"};
            case INDIAN_VEDIC:
                return new String[]{"Mrigashira (Orion head)", "Shravana (Altair)", "Abhijit (Vega)"};
            case JAPANESE:
                return new String[]{"Shēn 参 (Orion)", "Orihime 織姫 (Vega)", "Kengyuu 彦星 (Altair)"};
            case KOREAN:
                return new String[]{"삼수 Samsu (Orion)", "직녀 Jingnyeo (Vega)", "견우 Gyeonu (Altair)"};
            case BUGIS:
                return new String[]{"Bintang Tujuh (Pleiades)", "Bintang Pari (S.Cross)", "Bintang Beruang (Ursa)"};
            case ASIAN:
                return new String[]{"White Tiger (West)", "Black Tortoise (North)", "Azure Dragon (East)"};
            default:
                return new String[]{"Orion", "Summer Triangle", "Gemini"};
        }
    }
}

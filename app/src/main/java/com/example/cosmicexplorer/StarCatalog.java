package com.example.cosmicexplorer;

import java.util.ArrayList;
import java.util.List;

public class StarCatalog {

    public static class Star {
        public String name;
        public double ra;   // Right Ascension in degrees
        public double dec;  // Declination in degrees
        public float magnitude;
        public boolean isPlanet;
        public int color; // ARGB

        public Star(String name, double ra, double dec, float magnitude, boolean isPlanet, int color) {
            this.name = name;
            this.ra = ra;
            this.dec = dec;
            this.magnitude = magnitude;
            this.isPlanet = isPlanet;
            this.color = color;
        }
    }

    public static class Constellation {
        public String name;
        public int[] starIndices; // pairs of star indices = lines

        public Constellation(String name, int[] starIndices) {
            this.name = name;
            this.starIndices = starIndices;
        }
    }

    // 🌟 Bright Star Catalog (name, RA degrees, Dec degrees, magnitude)
    public static List<Star> getStars() {
        List<Star> stars = new ArrayList<>();

        // Stars — color: blue-white hot, yellow mid, orange/red cool
        stars.add(new Star("Sirius",     101.29, -16.72, -1.46f, false, 0xFFCCDDFF));
        stars.add(new Star("Canopus",    95.99,  -52.70, -0.72f, false, 0xFFFFFFCC));
        stars.add(new Star("Arcturus",   213.92,  19.18, -0.04f, false, 0xFFFFCC88));
        stars.add(new Star("Vega",       279.23,  38.78,  0.03f, false, 0xFFCCDDFF));
        stars.add(new Star("Capella",     79.17,  45.99,  0.08f, false, 0xFFFFFFAA));
        stars.add(new Star("Rigel",       78.63,  -8.20,  0.12f, false, 0xFFCCDDFF));
        stars.add(new Star("Procyon",    114.83,   5.22,  0.34f, false, 0xFFFFFFDD));
        stars.add(new Star("Betelgeuse",  88.79,   7.41,  0.42f, false, 0xFFFF8844));
        stars.add(new Star("Achernar",    24.43, -57.24,  0.46f, false, 0xFFCCDDFF));
        stars.add(new Star("Hadar",      210.96, -60.37,  0.61f, false, 0xFFCCDDFF));
        stars.add(new Star("Altair",     297.70,   8.87,  0.77f, false, 0xFFFFFFEE));
        stars.add(new Star("Aldebaran",   68.98,  16.51,  0.85f, false, 0xFFFF9944));
        stars.add(new Star("Antares",    247.35, -26.43,  0.96f, false, 0xFFFF4422));
        stars.add(new Star("Spica",      201.30, -11.16,  0.97f, false, 0xFFCCDDFF));
        stars.add(new Star("Pollux",     116.33,  28.03,  1.14f, false, 0xFFFFCC88));
        stars.add(new Star("Fomalhaut",  344.41, -29.62,  1.16f, false, 0xFFCCDDFF));
        stars.add(new Star("Deneb",      310.36,  45.28,  1.25f, false, 0xFFFFFFFF));
        stars.add(new Star("Mimosa",     191.93, -59.69,  1.25f, false, 0xFFCCDDFF));
        stars.add(new Star("Regulus",    152.09,  11.97,  1.35f, false, 0xFFCCDDFF));
        stars.add(new Star("Castor",     113.65,  31.89,  1.57f, false, 0xFFFFFFFF));
        stars.add(new Star("Bellatrix",   81.28,   6.35,  1.64f, false, 0xFFCCDDFF));
        stars.add(new Star("Elnath",      81.57,  28.61,  1.65f, false, 0xFFCCDDFF));
        stars.add(new Star("Miaplacidus",138.30, -69.72,  1.67f, false, 0xFFCCDDFF));
        stars.add(new Star("Alnilam",     84.05,  -1.20,  1.69f, false, 0xFFCCDDFF));
        stars.add(new Star("Alnitak",     85.19,  -1.94,  1.74f, false, 0xFFCCDDFF));
        stars.add(new Star("Mintaka",     83.00,  -0.30,  2.23f, false, 0xFFCCDDFF));

        // 🪐 Planets (approximate static positions — good enough for demo)
        stars.add(new Star("Mars",       120.00,  18.00,  0.5f,  true,  0xFFFF5533));
        stars.add(new Star("Jupiter",    45.00,   15.00,  -2.0f, true,  0xFFFFDDAA));
        stars.add(new Star("Saturn",     310.00,  -18.00, 0.8f,  true,  0xFFFFEEAA));
        stars.add(new Star("Venus",      60.00,   20.00, -4.0f,  true,  0xFFFFFFCC));
        stars.add(new Star("Mercury",    75.00,   22.00,  0.0f,  true,  0xFFCCCCCC));

        return stars;
    }

    // ✨ Constellation line data (pairs of indices into getStars())
    public static List<Constellation> getConstellations() {
        List<Constellation> constellations = new ArrayList<>();

        // Orion: Betelgeuse(7)-Alnilam(23), Alnilam(23)-Rigel(5),
        //        Alnilam(23)-Bellatrix(20), Alnilam(23)-Alnitak(24)
        constellations.add(new Constellation("Orion", new int[]{
                7, 23,   // Betelgeuse - Alnilam
                23, 5,   // Alnilam - Rigel
                23, 20,  // Alnilam - Bellatrix
                23, 24,  // Alnilam - Alnitak
                24, 25   // Alnitak - Mintaka
        }));

        // Summer Triangle: Vega(3)-Altair(10), Altair(10)-Deneb(16), Deneb(16)-Vega(3)
        constellations.add(new Constellation("Summer Triangle", new int[]{
                3, 10,
                10, 16,
                16, 3
        }));

        // Gemini: Castor(19)-Pollux(14)
        constellations.add(new Constellation("Gemini", new int[]{
                19, 14
        }));

        return constellations;
    }
}
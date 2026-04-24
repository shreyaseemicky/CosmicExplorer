package com.example.cosmicexplorer;

import java.util.ArrayList;
import java.util.List;

public class DeepSkyObject {

    public enum DSType {
        GALAXY, NEBULA, CLUSTER, SUPERNOVA_REMNANT
    }

    public String name;
    public double ra;        // Right Ascension degrees
    public double dec;       // Declination degrees
    public float  magnitude;
    public DSType type;
    public float  angularSize; // apparent size in degrees
    public int    coreColor;
    public int    glowColor;
    public String description;

    public DeepSkyObject(String name, double ra, double dec,
                         float magnitude, DSType type,
                         float angularSize,
                         int coreColor, int glowColor,
                         String description) {
        this.name        = name;
        this.ra          = ra;
        this.dec         = dec;
        this.magnitude   = magnitude;
        this.type        = type;
        this.angularSize = angularSize;
        this.coreColor   = coreColor;
        this.glowColor   = glowColor;
        this.description = description;
    }

    public static List<DeepSkyObject> getCatalog() {
        List<DeepSkyObject> list = new ArrayList<>();

        // ── GALAXIES ──────────────────────────────────────

        // Andromeda Galaxy M31 — large, tilted ellipse, pink/purple
        list.add(new DeepSkyObject(
                "Andromeda Galaxy", 10.68, 41.27,
                3.44f, DSType.GALAXY, 3.0f,
                0xFFFFCCDD, 0xFF661133,
                "The nearest major galaxy to the Milky Way, 2.537 million light-years away. Visible to the naked eye on dark nights."));

        // Triangulum Galaxy M33
        list.add(new DeepSkyObject(
                "Triangulum Galaxy", 23.46, 30.66,
                5.72f, DSType.GALAXY, 1.2f,
                0xFFCCDDFF, 0xFF223366,
                "The third-largest galaxy in the Local Group. Has an unusually prominent central star-forming region."));

        // Whirlpool Galaxy M51
        list.add(new DeepSkyObject(
                "Whirlpool Galaxy", 202.47, 47.20,
                8.4f, DSType.GALAXY, 0.6f,
                0xFFDDCCFF, 0xFF332255,
                "A classic spiral galaxy interacting with its smaller companion NGC 5195. One of the most photographed galaxies."));

        // Sombrero Galaxy M104
        list.add(new DeepSkyObject(
                "Sombrero Galaxy", 189.99, -11.62,
                8.0f, DSType.GALAXY, 0.5f,
                0xFFFFEECC, 0xFF443311,
                "Distinguished by its bright nucleus and large central bulge surrounded by a dark dust lane."));

        // Large Magellanic Cloud
        list.add(new DeepSkyObject(
                "Large Magellanic Cloud", 80.89, -69.76,
                0.9f, DSType.GALAXY, 5.5f,
                0xFFFFDDEE, 0xFF882244,
                "An irregular satellite galaxy of the Milky Way, 160,000 light-years away. Contains the Tarantula Nebula."));

        // Small Magellanic Cloud
        list.add(new DeepSkyObject(
                "Small Magellanic Cloud", 13.19, -72.83,
                2.7f, DSType.GALAXY, 3.5f,
                0xFFCCDDFF, 0xFF334466,
                "A dwarf galaxy and satellite of the Milky Way, 200,000 light-years away."));

        // Centaurus A
        list.add(new DeepSkyObject(
                "Centaurus A", 201.37, -43.02,
                6.84f, DSType.GALAXY, 0.6f,
                0xFFFFCCCC, 0xFF663322,
                "The nearest radio galaxy to Earth. Has a dramatic dark dust lane across its center and shoots powerful jets of particles."));

        // M81 Bode's Galaxy
        list.add(new DeepSkyObject(
                "Bode's Galaxy", 148.89, 69.07,
                6.94f, DSType.GALAXY, 0.7f,
                0xFFFFEEDD, 0xFF553322,
                "One of the brightest galaxies visible from Earth. Shows a classic grand design spiral structure."));

        // M82 Cigar Galaxy
        list.add(new DeepSkyObject(
                "Cigar Galaxy", 148.97, 69.68,
                8.41f, DSType.GALAXY, 0.5f,
                0xFFFFCC88, 0xFF883311,
                "A starburst galaxy with a rate of star formation 10 times higher than normal. Red hydrogen filaments stream out from its center."));

        // ── NEBULAE ───────────────────────────────────────

        // Orion Nebula M42
        list.add(new DeepSkyObject(
                "Orion Nebula", 83.82, -5.39,
                4.0f, DSType.NEBULA, 1.1f,
                0xFFFFEEFF, 0xFF8833AA,
                "The nearest stellar nursery to Earth at 1,344 light-years. Thousands of young stars are forming within it."));

        // Crab Nebula M1
        list.add(new DeepSkyObject(
                "Crab Nebula", 83.63, 22.01,
                8.4f, DSType.SUPERNOVA_REMNANT, 0.3f,
                0xFFFFCCAA, 0xFF884422,
                "The remnant of a supernova explosion recorded by Chinese astronomers in 1054 AD. Its pulsar spins 30 times per second."));

        // Lagoon Nebula M8
        list.add(new DeepSkyObject(
                "Lagoon Nebula", 270.92, -24.38,
                6.0f, DSType.NEBULA, 0.8f,
                0xFFFFAACC, 0xFF882244,
                "A giant interstellar cloud in Sagittarius, 4,100 light-years away. Glows pink from hydrogen gas excited by young stars."));

        // Eagle Nebula M16
        list.add(new DeepSkyObject(
                "Eagle Nebula", 274.70, -13.81,
                6.0f, DSType.NEBULA, 0.6f,
                0xFFAADDFF, 0xFF224488,
                "Home of the Pillars of Creation — vast columns of gas and dust where new stars are born."));

        // Ring Nebula M57
        list.add(new DeepSkyObject(
                "Ring Nebula", 283.40, 33.03,
                8.8f, DSType.NEBULA, 0.2f,
                0xFF88FFCC, 0xFF115533,
                "A planetary nebula — the outer shell of a dying star expelled into space. The central white dwarf is visible through a telescope."));

        // Helix Nebula NGC 7293
        list.add(new DeepSkyObject(
                "Helix Nebula", 337.41, -20.84,
                7.3f, DSType.NEBULA, 0.7f,
                0xFF88EEFF, 0xFF114455,
                "The largest planetary nebula in apparent size. Known as the Eye of God. Located 650 light-years away."));

        // Eta Carinae Nebula
        list.add(new DeepSkyObject(
                "Eta Carinae Nebula", 161.26, -59.68,
                1.0f, DSType.NEBULA, 2.5f,
                0xFFFFCC88, 0xFF885522,
                "One of the largest and brightest nebulae known. Contains Eta Carinae, a hypergiant star on the edge of exploding."));

        // ── STAR CLUSTERS ─────────────────────────────────

        // Pleiades M45
        list.add(new DeepSkyObject(
                "Pleiades", 56.75, 24.12,
                1.6f, DSType.CLUSTER, 1.0f,
                0xFFCCDDFF, 0xFF334488,
                "The most famous star cluster in the sky. Known as the Seven Sisters. Mentioned in texts from ancient Greece, the Bible, and the Mahabharata."));

        // Beehive Cluster M44
        list.add(new DeepSkyObject(
                "Beehive Cluster", 130.05, 19.67,
                3.7f, DSType.CLUSTER, 0.8f,
                0xFFFFFFCC, 0xFF445533,
                "One of the nearest open clusters. Contains over 1,000 stars. Known since antiquity as Praesepe — the Manger."));

        // Omega Centauri NGC 5139
        list.add(new DeepSkyObject(
                "Omega Centauri", 201.69, -47.48,
                3.9f, DSType.CLUSTER, 0.9f,
                0xFFFFEECC, 0xFF554422,
                "The largest and most massive globular cluster in the Milky Way. Contains about 10 million stars packed into a sphere 150 light-years across."));

        // Jewel Box NGC 4755
        list.add(new DeepSkyObject(
                "Jewel Box", 193.40, -60.35,
                4.2f, DSType.CLUSTER, 0.4f,
                0xFFCCEEFF, 0xFF224455,
                "A young open cluster with stars of contrasting colors — blue-white supergiants and a single red supergiant. One of the most colorful clusters."));

        // Globular M13 — Hercules
        list.add(new DeepSkyObject(
                "Hercules Cluster", 250.42, 36.46,
                5.8f, DSType.CLUSTER, 0.5f,
                0xFFFFFFDD, 0xFF445544,
                "The showpiece globular cluster of the northern sky. Contains several hundred thousand stars in a sphere 145 light-years across."));

        return list;
    }
}

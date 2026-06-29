package com.example.train_cal_b;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/station")
@CrossOrigin(origins = "http://localhost:4200")
public class TrainCalController {

    private final List<Map<String, Object>> stations;
    private final Map<String, String> stationNames;
    private final Map<String, String> stationLineNames;

    public TrainCalController(ObjectMapper objectMapper) throws Exception {
        try (var in = getClass().getResourceAsStream("/data.json")) {
            this.stations = objectMapper.readValue(in, new TypeReference<Map<String, List<Map<String, Object>>>>() {
            })
                    .get("stations");
        }
        this.stationNames = stations.stream()
                .collect(Collectors.toMap(
                        s -> (String) s.get("id"),
                        s -> (String) s.get("name")));
        this.stationLineNames = stations.stream()
                .collect(Collectors.toMap(
                        s -> (String) s.get("id"),
                        s -> (String) s.get("lineName")));
        this.lineArrays = Map.of(
                "bts_sukhumvit", bts_sukhumvit, "bts_silom", bts_silom,
                "blue", blue, "purple", purple,
                "yellow", yellow, "pink", pink, "gold", gold);
    }

    private final Map<String, List<String>> lineArrays;

    private final List<String> bts_sukhumvit = List.of(
            "N24", "N23", "N22", "N21", "N20", "N19", "N18", "N17", "N16", "N15",
            "N14", "N13", "N12", "N11", "N10", "N9", "N8", "N7", "N5",
            "N4", "N3", "N2", "N1", "CEN", "E1", "E2", "E3", "E4", "E5",
            "E6", "E7", "E8", "E9", "E10", "E11", "E12", "E13", "E14", "E15",
            "E16", "E17", "E18", "E19", "E20", "E21", "E22", "E23");

    private final List<String> bts_silom = List.of(
            "W1", "CEN", "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10", "S11", "S12");

    private final List<String> blue = List.of(
            "BL01", "BL02", "BL03", "BL04", "BL05", "BL06", "BL07", "BL08", "BL09", "BL10", "BL11", "BL12", "BL13",
            "BL14", "BL15", "BL16", "BL17", "BL18", "BL19", "BL20", "BL21", "BL22", "BL23", "BL24", "BL25", "BL26",
            "BL27", "BL28", "BL29", "BL30", "BL31", "BL32", "BL33", "BL34", "BL35", "BL36", "BL37", "BL38");

    private final List<String> purple = List.of(
            "PP01", "PP02", "PP03", "PP04", "PP05", "PP06", "PP07", "PP08", "PP09", "PP10",
            "PP11", "PP12", "PP13", "PP14", "PP15", "PP16");

    private final List<String> yellow = List.of(
            "YL01", "YL02", "YL03", "YL04", "YL05", "YL06", "YL07", "YL08", "YL09", "YL10",
            "YL11", "YL12", "YL13", "YL14", "YL15", "YL16", "YL17", "YL18", "YL19", "YL20",
            "YL21", "YL22", "YL23");

    private final List<String> pink = List.of(
            "PK01", "PK02", "PK03", "PK04", "PK05", "PK06", "PK07", "PK08", "PK09", "PK10",
            "PK11", "PK12", "PK13", "PK14", "PK15", "PK16", "PK17", "PK18", "PK19", "PK20",
            "PK21", "PK22", "PK23", "PK24", "PK25", "PK26", "PK27", "PK28", "PK29", "PK30");

    private final List<String> gold = List.of("G1", "G2", "G3");

    private final List<String> bts_main = List.of(
            "N8", "N7", "N5",
            "N4", "N3", "N2", "N1", "CEN", "E1", "E2", "E3", "E4", "E5",
            "E6", "E7", "E8", "E9", "W1", "CEN", "S1", "S2", "S3", "S4", "S5", "S6");

    private final List<String[]> ic = List.of(
            new String[] { "N8", "BL13" }, new String[] { "N9", "BL14" }, new String[] { "E4", "BL22" },
            new String[] { "S2", "BL26" }, new String[] { "S12", "BL34" },
            new String[] { "PP16", "BL10" }, new String[] { "PP11", "PK01" },
            new String[] { "PK16", "N17" }, new String[] { "YL01", "BL15" }, new String[] { "YL23", "E15" },
            new String[] { "G1", "S7" },
            new String[] { "BL01", "BL33" },
            new String[] { "BL01", "BL32" });

    private static final int[] FARE_PURPLE = { 14, 17, 20, 21, 25, 27, 30, 32, 35, 36, 38, 38 };
    private static final int[] FARE_BLUE = { 17, 20, 22, 25, 27, 29, 32, 34, 37, 39, 42, 44 };
    private static final int[] FARE_YELLOW = { 15, 18, 23, 28, 30, 34, 37, 41, 44, 45 };
    private static final int[] FARE_PINK = { 15, 18, 23, 28, 30, 34, 37, 41, 44, 45 };
    private static final int[] FARE_MAIN_BTS = { 17, 25, 28, 32, 35, 40, 43, 47 };
    private static final int[] FARE_ADDITIONAL_BTS = { 17, 19, 22, 24, 27, 29, 32, 34, 37, 39, 42, 44, 45 };
    private static final int FARE_GOLD = 17;

    private String findLineName(String id) {
        if (bts_sukhumvit.contains(id))
            return "bts_sukhumvit";
        if (bts_silom.contains(id))
            return "bts_silom";
        if (blue.contains(id))
            return "blue";
        if (purple.contains(id))
            return "purple";
        if (yellow.contains(id))
            return "yellow";
        if (pink.contains(id))
            return "pink";
        if (gold.contains(id))
            return "gold";
        return null;
    }

    private static int bts_main_count = 0;
    private static int blue_count = 0;
    private static int purple_count = 0;
    private static int yellow_count = 0;
    private static int pink_count = 0;
    private static int gold_count = 0;
    private static int bts_additional_count = 0;
    private int mrtToYpCount;

    private void findLineNameFare(String id) {
        if (bts_main.contains(id))
            bts_main_count += 1;
        else if (blue.contains(id))
            blue_count += 1;
        else if (purple.contains(id))
            purple_count += 1;
        else if (yellow.contains(id))
            yellow_count += 1;
        else if (pink.contains(id))
            pink_count += 1;
        else if (gold.contains(id))
            gold_count += 1;
        else
            bts_additional_count += 1;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return stations;
    }

    @PostMapping("v2/path")
    public Map<String, Object> calcPath(@RequestParam String start, @RequestParam String destination) {
        start = start.trim().toUpperCase();
        destination = destination.trim().toUpperCase();

        var graph = buildGraph();
        List<String> path = bfs(graph, start, destination);
        if (path == null)
            path = List.of();

        List<Map<String, String>> changes = new ArrayList<>();
        String prevId = null;
        String prevLine = null;
        for (String id : path) {
            String line = findLineName(id);
            if (prevLine != null && !prevLine.equals(line)) {
                String atId = lineArrays.get(line).contains(prevId) ? prevId : id;
                changes.add(Map.of(
                        "at", stationNames.get(atId),
                        "to", atId,
                        "toLine", line,
                        "toLineName", stationLineNames.get(id)));
            }
            prevId = id;
            prevLine = line;
        }

        var stationObjects = path.stream()
                .map(id -> Map.of(
                        "id", id,
                        "name", stationNames.get(id),
                        "lineName", stationLineNames.get(id)))
                .toList();

        return Map.of("station", stationObjects, "total", path.size() - 1, "changes", changes, "fare", calcFare());
    }

    private List<String> bfs(Map<String, Set<String>> graph, String start, String end) {
        Queue<String> q = new LinkedList<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> seen = new HashSet<>();
        bts_main_count = 0;
        blue_count = 0;
        purple_count = 0;
        yellow_count = 0;
        pink_count = 0;
        gold_count = 0;
        bts_additional_count = 0;
        mrtToYpCount = 0;

        q.add(start);
        seen.add(start);
        prev.put(start, null);
        while (!q.isEmpty()) {
            String cur = q.poll();
            if (cur.equals(end))
                break;
            for (String nxt : graph.getOrDefault(cur, Set.of()))
                if (!seen.contains(nxt)) {
                    seen.add(nxt);
                    prev.put(nxt, cur);
                    q.add(nxt);
                }
        }
        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = prev.get(at)) {
            if (!path.isEmpty())
                findLineNameFare(at);
            path.add(at);
            String prevId = prev.get(at);
            if (prevId != null) {
                String curLine = findLineName(at);
                String prevLine = findLineName(prevId);
                if ((curLine.equals("yellow") || curLine.equals("pink"))
                        && (prevLine.equals("blue") || prevLine.equals("purple"))) {
                    mrtToYpCount++;
                }
            }
        }
        Collections.reverse(path);
        System.out.println(path);
        return path.get(0).equals(start) ? path : null;
    }

    private HashMap<String, Set<String>> buildGraph() {
        HashMap<String, Set<String>> graph = new HashMap<>();
        for (var entry : lineArrays.entrySet()) {
            var ids = entry.getValue();
            for (int i = 0; i < ids.size() - 1; i++) {
                graph.computeIfAbsent(ids.get(i), k -> new HashSet<>()).add(ids.get(i + 1));
                graph.computeIfAbsent(ids.get(i + 1), k -> new HashSet<>()).add(ids.get(i));
            }
        }
        for (var pair : ic) {
            graph.computeIfAbsent(pair[0], k -> new HashSet<>()).add(pair[1]);
            graph.computeIfAbsent(pair[1], k -> new HashSet<>()).add(pair[0]);
        }
        return graph;
    }

    private int calcFare() {
        System.out.println(bts_main_count);
        System.out.println(bts_additional_count);
        bts_main_count = lookup(FARE_MAIN_BTS, bts_main_count);
        blue_count = lookup(FARE_BLUE, blue_count);
        purple_count = lookup(FARE_PURPLE, purple_count);
        yellow_count = lookup(FARE_YELLOW, yellow_count);
        pink_count = lookup(FARE_PINK, pink_count);
        if (gold_count > 0)
            gold_count = FARE_GOLD;
        bts_additional_count = lookup(FARE_ADDITIONAL_BTS, bts_additional_count);
        int bts_cal = Math.min(bts_main_count + bts_additional_count, 65) + gold_count;

        int yp_fare = yellow_count + pink_count;
        if (yp_fare > 0 && mrtToYpCount > 0)
            yp_fare *= -15;
        int mrt_cal = blue_count + purple_count
                - (blue_count > 0 && purple_count > 0 ? 14 : 0);
        return bts_cal + mrt_cal + yp_fare;
    }

    private int lookup(int[] table, int n) {
        int result = 0;
        if (n <= 0)
            return 0;
        else if (n <= table.length) {
            result = table[n - 1];
        } else {
            result = table[table.length - 1];
        }
        return result;
    }

}

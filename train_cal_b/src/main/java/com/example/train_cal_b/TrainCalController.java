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
                "blue_charan", blue_charan, "blue_main", blue_main,
                "blue_south", blue_south, "purple", purple,
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

    private final List<String> blue_charan = List.of(
            "BL01", "BL02", "BL03", "BL04", "BL05", "BL06", "BL07", "BL08", "BL09");

    private final List<String> blue_main = List.of(
            "BL32", "BL31", "BL30", "BL29", "BL28", "BL27", "BL26", "BL25", "BL24", "BL23", "BL22", "BL21", "BL20",
            "BL19", "BL18", "BL17", "BL16", "BL15", "BL14", "BL13", "BL12", "BL11", "BL10");

    private final List<String> blue_south = List.of(
            "BL38", "BL37", "BL36", "BL35", "BL34", "BL33");

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

    private final List<String[]> ic = List.of(
            new String[] { "N8", "BL13" }, new String[] { "N9", "BL14" }, new String[] { "E4", "BL22" },
            new String[] { "S2", "BL26" }, new String[] { "S12", "BL34" },
            new String[] { "PP16", "BL10" }, new String[] { "PP11", "PK01" },
            new String[] { "PK16", "N17" }, new String[] { "YL01", "BL15" }, new String[] { "YL23", "E15" },
            new String[] { "G1", "S7" },
            new String[] { "BL09", "BL10" }, new String[] { "BL32", "BL33" }, new String[] { "BL01", "BL33" },
            new String[] { "BL01", "BL32" });

    private String findLineName(String id) {
        if (bts_sukhumvit.contains(id))
            return "bts_sukhumvit";
        if (bts_silom.contains(id))
            return "bts_silom";
        if (blue_charan.contains(id))
            return "blue_charan";
        if (blue_main.contains(id))
            return "blue_main";
        if (blue_south.contains(id))
            return "blue_south";
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

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return stations;
    }

    @PostMapping("v2/path")
    public Map<String, Object> calcPath(@RequestParam String start, @RequestParam String destination) {
        start = start.trim().toUpperCase();
        destination = destination.trim().toUpperCase();
        System.out.println("v2: " + start + " → " + destination);

        var graph = buildGraph();
        List<String> path = bfs(graph, start, destination);
        if (path == null)
            path = List.of();

        List<Map<String, String>> changes = new ArrayList<>();
        String prevLine = null;
        for (String id : path) {
            String line = findLineName(id);
            if (prevLine != null && !prevLine.equals(line))
                changes.add(Map.of(
                        "at", stationNames.get(id),
                        "to", id,
                        "toLine", line,
                        "toLineName", stationLineNames.get(id)));
            prevLine = line;
        }

        var stationObjects = path.stream()
                .map(id -> Map.of(
                        "id", id,
                        "name", stationNames.get(id),
                        "lineName", stationLineNames.get(id)))
                .toList();

        return Map.of("station", stationObjects, "total", path.size(), "changes", changes);
    }

    private List<String> bfs(Map<String, Set<String>> graph, String start, String end) {
        Queue<String> q = new LinkedList<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> seen = new HashSet<>();
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
        for (String at = end; at != null; at = prev.get(at))
            path.add(at);
        Collections.reverse(path);
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

}

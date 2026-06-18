package com.example.train_cal_b;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api/station")
@CrossOrigin(origins = "http://localhost:4200")
public class TrainCalController {

    private final List<Map<String, Object>> stations;

    public TrainCalController(ObjectMapper objectMapper) throws Exception {
        try (var in = getClass().getResourceAsStream("/data.json")) {
            this.stations = objectMapper.readValue(in, new TypeReference<Map<String, List<Map<String, Object>>>>() {
            })
                    .get("stations");
        }
        this.lineArrays = Map.of(
                "bts_sukhumvit", bts_sukhumvit, "bts_silom", bts_silom,
                "blue_charan", blue_charan, "blue_main", blue_main,
                "blue_south", blue_south, "purple", purple,
                "yellow", yellow, "pink", pink, "gold", gold);
    }

    private final Map<String, List<String>> lineArrays;
    // ponytail: static line adjacency, update if lines change
    private final Map<String, Set<String>> lineAdj = Map.ofEntries(
            Map.entry("bts_sukhumvit", Set.of("bts_silom", "blue_main", "pink", "yellow")),
            Map.entry("bts_silom", Set.of("bts_sukhumvit", "blue_main", "blue_south", "gold")),
            Map.entry("blue_charan", Set.of("blue_main", "blue_south")),
            Map.entry("blue_main",
                    Set.of("bts_sukhumvit", "bts_silom", "blue_charan", "blue_south", "purple", "yellow")),
            Map.entry("blue_south", Set.of("bts_silom", "blue_charan", "blue_main")),
            Map.entry("purple", Set.of("blue_main", "pink")),
            Map.entry("pink", Set.of("bts_sukhumvit", "purple")),
            Map.entry("yellow", Set.of("bts_sukhumvit", "blue_main")),
            Map.entry("gold", Set.of("bts_silom")));

    private final List<String> bts_sukhumvit = List.of(
            "N24", "N23", "N22", "N21", "N20", "N19", "N18", "N17", "N16", "N15",
            "N14", "N13", "N12", "N11", "N10", "N9", "N8", "N7", "N6", "N5",
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

    // ponytail: list avoids Map.ofEntries duplicate-key crash (BL01 appears twice)
    private final List<String[]> ic = List.of(
            new String[] { "N8", "BL13" }, new String[] { "N9", "BL14" }, new String[] { "E4", "BL22" },
            new String[] { "S2", "BL26" }, new String[] { "S12", "BL34" },
            new String[] { "PP16", "BL10" }, new String[] { "PP11", "PK01" },
            new String[] { "PK16", "N17" }, new String[] { "YL01", "BL15" }, new String[] { "YL23", "E15" },
            new String[] { "G1", "S7" },
            new String[] { "BL09", "BL10" }, new String[] { "BL32", "BL33" }, new String[] { "BL01", "BL33" },
            new String[] { "BL01", "BL32" });

    private void appendSlice(List<String> path, List<String> slice) {
        if (!path.isEmpty() && !slice.isEmpty() && path.get(path.size() - 1).equals(slice.get(0)))
            slice = slice.subList(1, slice.size());
        path.addAll(slice);
    }

    // ponytail: BFS on ≤9 line nodes
    private List<String> routeLines(String from, String to) {
        Queue<String> q = new LinkedList<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> seen = new HashSet<>();
        q.add(from);
        seen.add(from);
        while (!q.isEmpty()) {
            String cur = q.poll();
            if (cur.equals(to))
                break;
            for (String nxt : lineAdj.getOrDefault(cur, Set.of()))
                if (!seen.contains(nxt)) {
                    seen.add(nxt);
                    prev.put(nxt, cur);
                    q.add(nxt);
                }
        }
        List<String> path = new ArrayList<>();
        for (String at = to; at != null; at = prev.get(at))
            path.add(at);
        Collections.reverse(path);
        return path;
    }

    // ponytail: scan ic list, 15 entries, O(n) per call, n never grows
    private String[] bestTransfer(String lineA, String lineB, String curId) {
        List<String> ids = lineArrays.get(lineA);
        int curIdx = ids.indexOf(curId);
        String from = curId, to = "";
        int bestD = Integer.MAX_VALUE;

        if (ids.contains("CEN") && lineArrays.get(lineB).contains("CEN")) {
            bestD = Math.abs(curIdx - ids.indexOf("CEN"));
            from = "CEN";
            to = "CEN";
        }

        for (var p : ic) {
            String aId = p[0], bId = p[1];
            if (!ids.contains(aId) && !ids.contains(bId))
                continue;
            if (!lineArrays.get(lineB).contains(aId) && !lineArrays.get(lineB).contains(bId))
                continue;
            String sa = ids.contains(aId) ? aId : bId;
            String sb = lineArrays.get(lineB).contains(aId) ? aId : bId;
            int d = Math.abs(curIdx - ids.indexOf(sa));
            if (d < bestD) {
                bestD = d;
                from = sa;
                to = sb;
            }
        }
        return new String[] { from, to };
    }

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

    private List<String> findLineArray(String id) {
        if (id.equals("CEN"))
            return bts_sukhumvit;
        if (bts_sukhumvit.contains(id))
            return bts_sukhumvit;
        if (bts_silom.contains(id))
            return bts_silom;
        if (blue_charan.contains(id))
            return blue_charan;
        if (blue_main.contains(id))
            return blue_main;
        if (blue_south.contains(id))
            return blue_south;
        if (purple.contains(id))
            return purple;
        if (yellow.contains(id))
            return yellow;
        if (pink.contains(id))
            return pink;
        if (gold.contains(id))
            return gold;
        return null;
    }

    @PostMapping("v2/path")
    public void calcPath(@RequestParam String start, @RequestParam String destination) {
        start = start.trim().toUpperCase();
        destination = destination.trim().toUpperCase();
        System.out.println(start + " " + destination);

        List<String> a = findLineArray(start);
        List<String> b = findLineArray(destination);

        if (a == null || b == null) {
            System.out.println("ไม่พบข้อมูลสถานี");
            return;
        }

        // case 1: same line
        if (a == b) {
            int n = Math.abs(a.indexOf(start) - a.indexOf(destination));
            System.out.println("สายเดียวกัน: " + n + " สถานี");
            return;
        }

        // case 2: BTS green cross (sukhumvit <-> silom via CEN)
        if ((a == bts_sukhumvit && b == bts_silom) || (a == bts_silom && b == bts_sukhumvit)) {
            int n = Math.abs(a.indexOf(start) - a.indexOf("CEN"))
                    + Math.abs(b.indexOf("CEN") - b.indexOf(destination));
            System.out.println("BTS ข้ามฝั่ง: เปลี่ยนที่สยาม, " + n + " สถานี");
            return;
        }

        // case 3: different lines — find shortest interchange
        int best = Integer.MAX_VALUE;
        String bx = "", by = "";
        for (var p : ic) {
            String x = p[0], y = p[1];
            if (a.contains(x) && b.contains(y)) {
                int n = Math.abs(a.indexOf(start) - a.indexOf(x))
                        + Math.abs(b.indexOf(y) - b.indexOf(destination));
                if (n < best) {
                    best = n;
                    bx = x;
                    by = y;
                }
            }
            if (a.contains(y) && b.contains(x)) {
                int n = Math.abs(a.indexOf(start) - a.indexOf(y))
                        + Math.abs(b.indexOf(x) - b.indexOf(destination));
                if (n < best) {
                    best = n;
                    bx = y;
                    by = x;
                }
            }
        }
        if (best < Integer.MAX_VALUE) {
            System.out.println("ต่างสาย: เปลี่ยนที่ " + bx + "/" + by + ", " + best + " สถานี");
            return;
        }

        System.out.println("ขออภัย ไม่พบเส้นทางระหว่าง " + start + " กับ " + destination);
    }

    private List<String> sliceLine(String lineName, String fromId, String toId) {
        var line = lineArrays.get(lineName);
        int fi = line.indexOf(fromId), ti = line.indexOf(toId);
        if (fi < ti)
            return new ArrayList<>(line.subList(fi, ti + 1));
        var slice = new ArrayList<>(line.subList(ti, fi + 1));
        Collections.reverse(slice);
        return slice;
    }

    @PostMapping("v4/path")
    public Map<String, Object> calcPathV4(@RequestParam String start, @RequestParam String destination) {
        start = start.trim().toUpperCase();
        destination = destination.trim().toUpperCase();
        System.out.println("v4: " + start + " → " + destination);

        String startLine = findLineName(start);
        String destLine = findLineName(destination);

        List<String> path = new ArrayList<>();
        List<Map<String, String>> changes = new ArrayList<>();

        if (startLine.equals(destLine)) {
            appendSlice(path, sliceLine(startLine, start, destination));
            System.out.println("สายเดียวกัน: " + (path.size() - 1) + " สถานี → " + String.join(", ", path));
        } else {
            List<String> lines = routeLines(startLine, destLine);
            String curId = start;
            for (int i = 0; i < lines.size() - 1; i++) {
                String[] tp = bestTransfer(lines.get(i), lines.get(i + 1), curId);
                appendSlice(path, sliceLine(lines.get(i), curId, tp[0]));
                changes.add(Map.of("at", tp[0], "to", tp[1], "toLine", lines.get(i + 1)));
                System.out.println("  " + lines.get(i) + " → เปลี่ยนที่ " + tp[0] + " ไป " + tp[1] + " ("
                        + lines.get(i + 1) + ")");
                curId = tp[1];
            }
            appendSlice(path, sliceLine(destLine, curId, destination));
            System.out.println("  " + lines.get(lines.size() - 1) + " → " + destination);
            var changeMsgs = changes.stream()
                    .map(c -> "เปลี่ยนที่ " + c.get("at") + " → " + c.get("toLine"))
                    .collect(java.util.stream.Collectors.joining(", "));
            System.out.println("รวม " + path.size() + " สถานี" + (changes.isEmpty() ? "" : " (" + changeMsgs + ")"));
        }

        return Map.of("path", path, "totalStations", path.size(), "changes", changes);
    }
}

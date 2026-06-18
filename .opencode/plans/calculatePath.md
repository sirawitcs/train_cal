# calculatePath implementation

## Changes to `TrainCalController.java`

### 1. Remove duplicate import
Delete: `import org.springframework.web.bind.annotation.PostMapping;` (already covered by `*` wildcard)

### 2. Replace `calculatePath` method and add helpers

Replace the old `getPrefix`, `getNumber`, and `calculatePath` with:

```java
private String getPrefix(String value) {
    return value.trim().toUpperCase().replaceAll("[0-9]", "");
}

private int getNumber(String value) {
    if (value.trim().equalsIgnoreCase("CEN")) return 0;
    return Integer.parseInt(value.replaceAll("[^0-9]", ""));
}

private int positionFromCen(String id) {
    if (id.equalsIgnoreCase("CEN")) return 0;
    return getNumber(id); // N, E, S, W all use station number as distance
}

@PostMapping("path")
public Map<String, Integer> calculatePath(@RequestParam String start, @RequestParam String destination) {
    String su = start.trim().toUpperCase();
    String du = destination.trim().toUpperCase();

    if (su.equals(du)) return Map.of("stationCount", 0);

    String sp = getPrefix(su);
    String dp = getPrefix(du);
    int spc = positionFromCen(su);
    int dpc = positionFromCen(du);

    int count;
    if (sp.equals(dp)) {
        count = Math.abs(spc - dpc);
    } else {
        count = spc + dpc;
    }

    return Map.of("stationCount", count);
}
```

### What changed
- `getNumber` now handles CEN (returns 0 instead of crashing)
- Added `positionFromCen` — distance from Siam for any BTS station
- Endpoint returns `Map<String, Integer>` JSON (`{"stationCount": 5}`)
- Same prefix → absolute difference; different prefix → sum of distances

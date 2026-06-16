package com.example.train_cal_b;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return stations;
    }
}
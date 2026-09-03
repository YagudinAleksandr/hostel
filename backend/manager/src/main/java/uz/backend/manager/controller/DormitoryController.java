package uz.backend.manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.backend.common.dto.dormitory.*;
import uz.backend.manager.service.DormitoryService;

import java.util.List;

/**
 * REST контроллер для общежитий
 *
 * @author Aleksandr Yagudin
 */
@RestController
@RequestMapping("/api/v1/dormitories")
@RequiredArgsConstructor
public class DormitoryController {
    private final DormitoryService dormitoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DormitoryResponseDto create(@Valid @RequestBody DormitoryCreateRequestDto request) {
        return dormitoryService.create(request);
    }

    @GetMapping
    public List<DormitorySummaryDto> all() {
        return dormitoryService.getListShortInfo();
    }

    @GetMapping("/{id}")
    public DormitoryResponseDto get(@PathVariable Long id) {
        return dormitoryService.get(id);
    }

    @PutMapping("/{id}")
    public DormitoryResponseDto update(@PathVariable Long id,
                                       @Valid @RequestBody DormitoryUpdateRequestDto request) {
        return dormitoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        dormitoryService.delete(id);
    }

    @PostMapping("/{id}/entrances")
    @ResponseStatus(HttpStatus.CREATED)
    public EntranceResponseDto addEntrance(@PathVariable Long id,
                                           @Valid @RequestBody EntranceCreateRequestDto request) {
        return dormitoryService.addEntrance(id, request);
    }

    @PutMapping("/{id}/entrances/{name}")
    public EntranceResponseDto renameEntrance(@PathVariable Long id,
                                              @PathVariable String name,
                                              @Valid @RequestBody EntranceUpdateRequestDto request) {
        return dormitoryService.renameEntrance(id, name, request);
    }

    @DeleteMapping("/{id}/entrances/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntrance(@PathVariable Long id,
                               @PathVariable String name) {
        dormitoryService.removeEntrance(id, name);
    }

    @PostMapping("/{id}/entrances/{entranceName}/floors")
    @ResponseStatus(HttpStatus.CREATED)
    public FloorResponseDto addFloor(@PathVariable Long id,
                                     @PathVariable String entranceName,
                                     @Valid @RequestBody FloorCreateRequestDto request) {
        return dormitoryService.addFloor(id, entranceName, request);
    }

    @PutMapping("/{id}/entrances/{entranceName}/floors/{floorName}")
    public FloorResponseDto renameFloor(@PathVariable Long id,
                                        @PathVariable String entranceName,
                                        @PathVariable String floorName,
                                        @Valid @RequestBody FloorUpdateRequestDto request) {
        return dormitoryService.renameFloor(id, entranceName, floorName, request);
    }

    @DeleteMapping("/{id}/entrances/{entranceName}/floors/{floorName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFloor(@PathVariable Long id,
                            @PathVariable String entranceName,
                            @PathVariable String floorName) {
        dormitoryService.removeFloor(id, entranceName, floorName);
    }
}

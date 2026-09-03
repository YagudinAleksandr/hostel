package uz.backend.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.backend.common.dto.dormitory.*;
import uz.backend.common.vo.Address;
import uz.backend.database.entity.Dormitory;
import uz.backend.database.entity.Entrance;
import uz.backend.database.entity.Floor;
import uz.backend.database.repository.DormitoryRepository;
import uz.backend.manager.exception.ConflictException;
import uz.backend.manager.exception.NotFoundException;
import uz.backend.manager.mapper.MapperBase;

import java.util.List;

/**
 * Сервис для работы с общежитиями
 *
 * @author Aleksandr Yagudin
 */
@Service
@RequiredArgsConstructor
public class DormitoryService {
    private final DormitoryRepository dormitoryRepository;
    private final MapperBase mapper;

    /**
     * Получить информацию общежития со всеми вложениями
     *
     * @param id идентификатор общежития
     * @return общежитие
     */
    @Transactional(readOnly = true)
    public DormitoryResponseDto get(Long id) {
        Dormitory dormitory = dormitoryRepository.findWithStructureById(id)
                .orElseThrow(() -> new NotFoundException("Общежитие " + id + " не найдено"));
        return mapper.toDto(dormitory, DormitoryResponseDto.class);
    }

    /**
     * Получить краткую информацию по общежитию
     *
     * @param id идентификатор общежития
     * @return краткая информация по общежитию
     */
    @Transactional(readOnly = true)
    public DormitorySummaryDto getShortInfo(Long id) {
        Dormitory dormitory = dormitoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Общежитие " + id + " не найдено"));
        return mapper.toDto(dormitory, DormitorySummaryDto.class);
    }

    /**
     * Получение списка общежитий (сокращенная информация)
     *
     * @return список общежитий (сокращенная информация)
     */
    public List<DormitorySummaryDto> getListShortInfo() {
        var dormitories = dormitoryRepository.findAll();
        return mapper.toDtoList(dormitories, DormitorySummaryDto.class);
    }

    /**
     * Создание общежития
     *
     * @param request запрос на создание общежития
     * @return общежитие
     */
    @Transactional
    public DormitoryResponseDto create(DormitoryCreateRequestDto request) {
        final Dormitory dormitory = new Dormitory(request.name(), mapper.map(request.address(), Address.class));
        return mapper.toDto(dormitoryRepository.save(dormitory), DormitoryResponseDto.class);
    }

    /**
     * Обновление информации об общежитии
     *
     * @param id      идентификатор общежития
     * @param request запрос на обновление
     * @return обновленная информация об общежитии
     */
    @Transactional
    public DormitoryResponseDto update(Long id, DormitoryUpdateRequestDto request) {
        final Dormitory dormitory = dormitoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Общежитие " + id + " не найдено"));

        checkVersion(dormitory, request.version());

        dormitory.rename(request.name());
        dormitory.relocate(mapper.map(request.address(), Address.class));

        return mapper.toDto(dormitory, DormitoryResponseDto.class);
    }

    /**
     * Удаление общежития
     *
     * @param id идентификатор общежития
     */
    @Transactional
    public void delete(Long id) {
        var dormitory = dormitoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Общежитие " + id + " не найдено"));
        dormitoryRepository.delete(dormitory);
    }

    //----- Подъезды ------

    /**
     * Добавление подъезда
     *
     * @param dormitoryId идентификатор общежития
     * @param request     запрос на создание подъезда
     * @return подъезд
     */
    @Transactional
    public EntranceResponseDto addEntrance(Long dormitoryId, EntranceCreateRequestDto request) {
        final Entrance entrance = dormitoryRepository.findWithStructureById(dormitoryId)
                .orElseThrow(() -> new NotFoundException("Общежитие " + dormitoryId + " не найдено"))
                .addEntrance(request.name());

        dormitoryRepository.flush();
        return mapper.toDto(entrance, EntranceResponseDto.class);
    }

    /**
     * Переименование подъезда
     *
     * @param dormitoryId идентификатор общежития
     * @param name        старое название подъезда
     * @param request     запрос на обновление названия подъезда
     * @return обновленная информация о подъезде
     */
    @Transactional
    public EntranceResponseDto renameEntrance(Long dormitoryId, String name, EntranceUpdateRequestDto request) {
        final Dormitory dormitory = dormitoryRepository.findWithStructureById(dormitoryId)
                .orElseThrow(() -> new NotFoundException("Общежитие " + dormitoryId + " не найдено"));

        final Entrance entrance = dormitory.findEntrance(name)
                .orElseThrow(() -> new NotFoundException("Подъезд " + name + " не найден"));
        entrance.setName(request.name());

        return mapper.toDto(entrance, EntranceResponseDto.class);
    }

    /**
     * Удаление подъезда
     *
     * @param dormitoryId идентификатор общежития
     * @param name        название подъезда
     */
    @Transactional
    public void removeEntrance(Long dormitoryId, String name) {
        final Dormitory dormitory = dormitoryRepository.findWithStructureById(dormitoryId)
                .orElseThrow(() -> new NotFoundException("Общежитие " + dormitoryId + " не найдено"));

        final Entrance entrance = dormitory.findEntrance(name)
                .orElseThrow(() -> new NotFoundException("Подъезд " + name + " не найден"));

        dormitory.removeEntrance(name);
    }

    // -------- Этажи -------

    /**
     * Добавление этажа
     *
     * @param dormitoryId  идентификатор общежития
     * @param entranceName название подъезда
     * @param request      запрос на создание этажа
     * @return этаж
     */
    @Transactional
    public FloorResponseDto addFloor(Long dormitoryId, String entranceName, FloorCreateRequestDto request) {
        final Floor floor = dormitoryRepository.findWithStructureById(dormitoryId)
                .orElseThrow(() -> new NotFoundException("Общежитие " + dormitoryId + " не найдено"))
                .findEntrance(entranceName)
                .orElseThrow(() -> new NotFoundException("Подъезд " + entranceName + " не найден"))
                .addFloor(request.name());

        dormitoryRepository.flush();

        return mapper.toDto(floor, FloorResponseDto.class);
    }

    /**
     * Обновление названия этажа
     *
     * @param dormitoryId  идентификатор общежития
     * @param entranceName название подъезда
     * @param floorName    название этажа
     * @param request      запрос на изменение названия этажа
     * @return этаж
     */
    @Transactional
    public FloorResponseDto renameFloor(Long dormitoryId, String entranceName, String floorName,
                                        FloorUpdateRequestDto request) {
        final Floor floor = dormitoryRepository.findWithStructureById(dormitoryId)
                .orElseThrow(() -> new NotFoundException("Общежитие " + dormitoryId + " не найдено"))
                .findEntrance(entranceName)
                .orElseThrow(() -> new NotFoundException("Подъезд " + entranceName + " не найден"))
                .findFloor(floorName)
                .orElseThrow(() -> new NotFoundException("Этаж " + floorName + " не найден"));
        floor.setName(request.name());

        return mapper.toDto(floor, FloorResponseDto.class);
    }

    /**
     * Удаление этажа
     *
     * @param dormitoryId  идентификатор общежития
     * @param entranceName название подъезда
     * @param floorName    название этажа
     */
    @Transactional
    public void removeFloor(Long dormitoryId, String entranceName, String floorName) {
        final Entrance entrance = dormitoryRepository.findWithStructureById(dormitoryId)
                .orElseThrow(() -> new NotFoundException("Общежитие " + dormitoryId + " не найдено"))
                .findEntrance(entranceName)
                .orElseThrow(() -> new NotFoundException("Подъезд " + entranceName + " не найден"));
        entrance.removeFloor(floorName);
    }

    /**
     * Сравнение примитива с распакованным Long:== между двумя Long сравнил бы ссылки
     *
     * @param dormitory общежитие
     * @param expected  пропущенная версия
     */
    private void checkVersion(Dormitory dormitory, Long expected) {
        if (expected == null || dormitory.getVersion() != expected.longValue()) {
            throw new ConflictException("Общежитие изменено другим пользователем");
        }
    }
}

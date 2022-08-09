package ntut.csie.sslab.ddd.entity;

import ntut.csie.sslab.ddd.entity.common.BiMap;

import java.util.Map;

public interface DomainEventTypeMapper {

    void put(String key, Class value);
    String toMappingType(Class cls);
    String toMappingType(DomainEvent event);
    boolean containsMappingType(String mappingType);
    Class<? extends DomainEvent> toClass(String mappingType);
    Map<String, Class> getMap();

    class DomainEventTypeMapperImpl implements DomainEventTypeMapper {

        private final BiMap<String, Class> biMapper;

        public DomainEventTypeMapperImpl() {
            biMapper = new BiMap<>();
        }

        @Override
        public void put(String key, Class value){
            biMapper.put(key, value);
        }


        @Override
        public String toMappingType(Class cls){
            if (null != biMapper.getKey(cls)) {
                return biMapper.getKey(cls);
            }
            throw new RuntimeException("Unsupported event for getting mapping: " + cls);
        }


        @Override
        public String toMappingType(DomainEvent event){
            if (null != biMapper.getKey(event.getClass())) {
                return biMapper.getKey(event.getClass());
            }
            throw new RuntimeException("Unsupported event for getting mapping: " + event);
        }

        @Override
        public boolean containsMappingType(String mappingType){
            return biMapper.containsKey(mappingType);
        }

        @Override
        public Class<? extends DomainEvent> toClass(String mappingType){
            if (biMapper.containsKey(mappingType)){
                return biMapper.get(mappingType);
            }
            throw new RuntimeException("Unsupported event mapping type: " + mappingType);
        }

        @Override
        public Map<String, Class> getMap() {
            return biMapper;
        }
    }

}

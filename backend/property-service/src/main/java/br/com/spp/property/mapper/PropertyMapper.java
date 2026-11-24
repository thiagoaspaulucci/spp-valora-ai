package br.com.spp.property.mapper;

import br.com.spp.property.dto.PropertyRequest;
import br.com.spp.property.dto.PropertyResponse;
import br.com.spp.property.entity.Property;
import br.com.spp.property.entity.PropertyStatus;
import org.springframework.stereotype.Component;

/**
 * Conversões entre a entidade {@link Property} e seus DTOs.
 */
@Component
public class PropertyMapper {

    public Property toEntity(PropertyRequest request, String ownerSubject) {
        return Property.builder()
                .title(request.title())
                .type(request.type())
                .street(request.street())
                .number(request.number())
                .complement(request.complement())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .areaSqm(request.areaSqm())
                .bedrooms(request.bedrooms())
                .bathrooms(request.bathrooms())
                .parkingSpots(request.parkingSpots())
                .yearBuilt(request.yearBuilt())
                .askingPrice(request.askingPrice())
                .status(PropertyStatus.DRAFT)
                .ownerSubject(ownerSubject)
                .build();
    }

    public void update(Property property, PropertyRequest request) {
        property.setTitle(request.title());
        property.setType(request.type());
        property.setStreet(request.street());
        property.setNumber(request.number());
        property.setComplement(request.complement());
        property.setNeighborhood(request.neighborhood());
        property.setCity(request.city());
        property.setState(request.state());
        property.setZipCode(request.zipCode());
        property.setAreaSqm(request.areaSqm());
        property.setBedrooms(request.bedrooms());
        property.setBathrooms(request.bathrooms());
        property.setParkingSpots(request.parkingSpots());
        property.setYearBuilt(request.yearBuilt());
        property.setAskingPrice(request.askingPrice());
    }

    public PropertyResponse toResponse(Property p) {
        return new PropertyResponse(
                p.getId(), p.getTitle(), p.getType(),
                p.getStreet(), p.getNumber(), p.getComplement(), p.getNeighborhood(),
                p.getCity(), p.getState(), p.getZipCode(),
                p.getAreaSqm(), p.getBedrooms(), p.getBathrooms(), p.getParkingSpots(),
                p.getYearBuilt(), p.getAskingPrice(), p.getStatus(), p.getOwnerSubject(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}

package org.example.sharing.rest.mapper;

import org.example.sharing.store.entity.Review;
import org.apache.axis.encoding.Base64;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UtilityMapper {

    @Named(value = "avgGetScore")
    default Double avgGetScore(List<Review> ratingsList) {
        return Optional.ofNullable(ratingsList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .mapToDouble(Review::getScore)
                .average()
                .orElse(0d);
    }

    @Named(value = "encode")
    default String encode(byte[] bytes) {
        return Base64.encode(bytes);
    }

    @Named(value = "decode")
    default byte[] decode(String bytes) {
        return Base64.decode(bytes);
    }
}

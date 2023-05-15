package org.example.sharing.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerBlackListResponseDto {

    public UUID ownerUserId;

    public List<BlackListUserResponseDto> blackList;
}

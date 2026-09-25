package com.simaomonteiro18.lisbonpitches.mappers;

import com.simaomonteiro18.lisbonpitches.dtos.UserSummaryDTO;
import com.simaomonteiro18.lisbonpitches.entities.User;

public class UserMapper {

    public static UserSummaryDTO toDTO(User user) {

        UserSummaryDTO userSummaryDTO = new UserSummaryDTO(user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getPhone(), user.getCity());

        return userSummaryDTO;

    }

}

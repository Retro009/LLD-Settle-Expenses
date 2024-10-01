package com.example.splitwise.controllers;


import com.example.splitwise.dtos.*;
import com.example.splitwise.exceptions.InvalidGroupException;
import com.example.splitwise.exceptions.InvalidUserException;
import com.example.splitwise.services.SettleUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SettleUpController {

    @Autowired
    SettleUpService settleUpService;

    public SettleGroupResponseDto settleGroup(SettleGroupRequestDto dto){
        SettleGroupResponseDto responseDto = new SettleGroupResponseDto();
        try {
            responseDto.setTransactions(settleUpService.settleGroup(dto.getGroupId()));
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (InvalidGroupException e) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }
        return responseDto;
    }

    public SettleUserResponseDto settleUser(SettleUserRequestDto requestDto){
        SettleUserResponseDto responseDto = new SettleUserResponseDto();
        try {
            responseDto.setTransactions(settleUpService.settleUser(requestDto.getUserId()));
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (InvalidUserException e) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }
        return responseDto;
    }
}

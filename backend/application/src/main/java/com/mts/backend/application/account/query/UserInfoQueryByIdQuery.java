package com.mts.backend.application.account.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@SuperBuilder
public class UserInfoQueryByIdQuery extends AccountByIdQuery{
}

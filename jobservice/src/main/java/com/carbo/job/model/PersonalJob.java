package com.carbo.job.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalJob {

    private String jobId;

    private List<User> userList;
}

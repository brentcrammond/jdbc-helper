/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.h4t.jdbchelper;

import lombok.Builder;
import lombok.Data;

/**
 * Test Object, will contain the Name as an Object.
 */
@Data
@Builder
public class Name {
    private Long id;
    private String name;
    private int seq;
}

package com.idealista.restexporter.dummies;

import java.util.Date;

public class DummyServiceImpl implements DummyService {

    @Override
    public Date getDate() {
        return new Date();
    }
    
}
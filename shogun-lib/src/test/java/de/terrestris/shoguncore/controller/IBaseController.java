package de.terrestris.shoguncore.controller;

public interface IBaseController {
    void setBasePath();
    void setEntityClass();
    void insertTestData();
    void cleanupTestData();
}

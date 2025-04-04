package ru.cherry.itask.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void ReturnInitializedManagers() {
     assertNotNull(Managers.getDefault(), "Не должен быть Null в getDefault");
     assertNotNull(Managers.getDefaultHistory(), "Не должно быть Null в getDefaultHistory");
 }
}
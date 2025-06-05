package ru.cherry.itask.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
 private final Managers managers = new Managers();

 @Test
 void ReturnInitializedManagers() {
  assertNotNull(managers.getDefaultHistory(), "Не должно быть Null в getDefaultHistory");
 }
}
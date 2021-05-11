package com.prime.rushhour.service;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.repository.RoleRepository;
import com.prime.rushhour.services.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    public void addRoleTest() {
        Role role = new Role("ROLE_ADMIN");
        when(roleRepository.save(role)).thenReturn(role);
        roleService.add(role);
        verify(roleRepository, Mockito.times(1)).save(role);
    }

    @Test
    public void getAllTest() {
        Role role = new Role("ROLE_ADMIN");
        Role role2 = new Role("ROLE_USER");
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role, role2));
        List<Role> list = roleService.getAll();
        assertEquals(2, list.size());
    }
}

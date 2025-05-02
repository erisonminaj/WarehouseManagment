package com.warehouse;

import com.warehouse.model.ERole;
import com.warehouse.model.Permission;
import com.warehouse.model.Role;
import com.warehouse.repository.PermissionRepository;
import com.warehouse.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.HashSet;


@SpringBootApplication
@EnableScheduling
public class WarehouseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
            // Create permissions if they don't exist
            if (permissionRepository.count() == 0) {
                Permission viewOrders = new Permission();
                viewOrders.setName("VIEW_ORDERS");

                Permission createOrders = new Permission();
                createOrders.setName("CREATE_ORDERS");

                Permission updateOrders = new Permission();
                updateOrders.setName("UPDATE_ORDERS");

                Permission cancelOrders = new Permission();
                cancelOrders.setName("CANCEL_ORDERS");

                Permission approveOrders = new Permission();
                approveOrders.setName("APPROVE_ORDERS");

                Permission manageItems = new Permission();
                manageItems.setName("MANAGE_ITEMS");

                Permission scheduleDelivery = new Permission();
                scheduleDelivery.setName("SCHEDULE_DELIVERY");

                Permission manageUsers = new Permission();
                manageUsers.setName("MANAGE_USERS");

                permissionRepository.saveAll(Arrays.asList(
                        viewOrders, createOrders, updateOrders, cancelOrders,
                        approveOrders, manageItems, scheduleDelivery, manageUsers
                ));
            }

            if (roleRepository.count() == 0) {
                // Client role
                Role clientRole = new Role();
                clientRole.setName(ERole.CLIENT);
                clientRole.setPermissions(new HashSet<>(permissionRepository.findAllById(Arrays.asList(
                        permissionRepository.findByName("VIEW_ORDERS").get().getId(),
                        permissionRepository.findByName("CREATE_ORDERS").get().getId(),
                        permissionRepository.findByName("UPDATE_ORDERS").get().getId(),
                        permissionRepository.findByName("CANCEL_ORDERS").get().getId()
                ))));

                Role managerRole = new Role();
                managerRole.setName(ERole.WAREHOUSE_MANAGER);
                managerRole.setPermissions(new HashSet<>(permissionRepository.findAllById(Arrays.asList(
                        permissionRepository.findByName("VIEW_ORDERS").get().getId(),
                        permissionRepository.findByName("APPROVE_ORDERS").get().getId(),
                        permissionRepository.findByName("MANAGE_ITEMS").get().getId(),
                        permissionRepository.findByName("SCHEDULE_DELIVERY").get().getId()
                ))));

                Role adminRole = new Role();
                adminRole.setName(ERole.SYSTEM_ADMIN);
                adminRole.setPermissions(new HashSet<>(permissionRepository.findAllById(Arrays.asList(
                        permissionRepository.findByName("VIEW_ORDERS").get().getId(),
                        permissionRepository.findByName("APPROVE_ORDERS").get().getId(),
                        permissionRepository.findByName("MANAGE_ITEMS").get().getId(),
                        permissionRepository.findByName("SCHEDULE_DELIVERY").get().getId(),
                        permissionRepository.findByName("MANAGE_USERS").get().getId()
                ))));

                roleRepository.saveAll(Arrays.asList(clientRole, managerRole, adminRole));
            }
        };
    }
}

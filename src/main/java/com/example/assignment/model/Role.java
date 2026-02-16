package com.example.assignment.model;

/**
 * Role Enum - Defines the two types of users in our system
 *
 * STUDENT - Can only READ data (view profile, view courses)
 * TEACHER - Can CREATE, READ, UPDATE, DELETE data (full access)
 *
 * An enum is a special class that represents a fixed set of constants.
 * Think of it as a dropdown with only these two options.
 */
public enum Role {
    STUDENT,
    TEACHER
}

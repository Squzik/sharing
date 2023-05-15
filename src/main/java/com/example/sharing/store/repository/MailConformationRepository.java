package org.example.sharing.store.repository;

import org.example.sharing.store.entity.MailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MailConformationRepository extends JpaRepository<MailConfirmation, UUID> {
}

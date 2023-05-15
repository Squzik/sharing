package org.example.sharing.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(nullable = false)
    private UUID id;

    @Column(name = "surname")
    private String surname;

    @Column(name = "name")
    private String name;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mail")
    private String mail;

    @Column(name = "password")
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sciener_user_id")
    private ScienerUser scienerUser;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Flat> flats;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Booking> bookings;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstUser")
    private List<ChatRoom> firstUser;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondUser")
    private List<ChatRoom> secondUser;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sender")
    private List<ChatMessage> sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passport_verify_status_id")
    private PassportVerifyStatus passportVerifyStatus;

    @Column(name = "passport_file_id")
    private UUID passportFileId;

    @Column(name = "is_mail_confirm", nullable = false, insertable = false)
    private Boolean isMailConfirm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;

    @OneToOne(mappedBy = "user")
    private MailConfirmation mailConfirmation;

    @OneToMany(mappedBy = "ownerUser")
    private Set<BlackList> ownedBlackLists;

    @OneToMany(mappedBy = "user")
    private Set<BlackList> memberBlackLists;

    @OneToOne(mappedBy = "user")
    private File file;

    @ManyToMany(cascade =
                {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
                },
            targetEntity = Flat.class)
    @JoinTable(
            name = "favourites",
            inverseJoinColumns = @JoinColumn(name = "flat_id",
                    nullable = false,
                    updatable = false),
            joinColumns = @JoinColumn(name = "user_id",
                    nullable = false,
                    updatable = false)
    )
    private  Set<Flat> favouritesFlats = new HashSet<>();

}

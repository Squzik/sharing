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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "flat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flat {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_of_flat_id")
    private TypeOfFlat typeOfFlat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_of_rental_id")
    private TypeOfRental typeOfRental;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_of_building_id")
    private TypeOfBuilding typeOfBuilding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_status_id")
    private FlatStatus flatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @Column(name = "is_combined_bathroom")
    private Boolean isCombinedBathroom;

    @Column(name = "furniture")
    private Boolean furniture;

    @Column(name = "balcony")
    private Boolean balcony;

    @Column(name = "appliances")
    private Boolean appliances;

    @Column(name = "internet_cable_tv")
    private Boolean internetCableTv;

    @Column(name = "is_hidden")
    private Boolean isHidden;

    @Column(name = "price")
    private Double price;

    @OneToMany(mappedBy = "flat")
    private List<File> files;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sciener_lock_id")
    private ScienerLock scienerLock;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "flat")
    private List<Review> reviews;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "flat")
    private Set<Booking> bookings;

    @ManyToMany(cascade =
            {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST
            },
            targetEntity = User.class)
    @JoinTable(
            name = "favourites",
            joinColumns = @JoinColumn(name = "flat_id",
                    nullable = false,
                    updatable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id",
                    nullable = false,
                    updatable = false)
    )
    private Set<User> favouritesUsers = new HashSet<>();
}
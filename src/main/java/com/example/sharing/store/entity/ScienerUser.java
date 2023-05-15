package org.example.sharing.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "sciener_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScienerUser {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_end_time")
    private Long tokenEndTime;

    @OneToMany(mappedBy = "scienerUser")
    private Set<ScienerLock> locks;

    @OneToOne(mappedBy = "scienerUser")
    private User user;

    public void updateTokens(ScienerUser user) {
        this.accessToken = user.getAccessToken();
        this.refreshToken = user.getRefreshToken();
        this.tokenEndTime = user.getTokenEndTime();
    }
}

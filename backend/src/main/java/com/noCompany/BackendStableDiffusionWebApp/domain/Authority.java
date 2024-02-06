package com.noCompany.BackendStableDiffusionWebApp.domain;
import com.noCompany.BackendStableDiffusionWebApp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Authority implements GrantedAuthority {

    private Role authority;

    @Override
    public String getAuthority() {
        return authority.name();
    }
}

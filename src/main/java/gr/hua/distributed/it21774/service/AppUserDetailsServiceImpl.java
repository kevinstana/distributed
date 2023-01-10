package gr.hua.distributed.it21774.service;

import gr.hua.distributed.it21774.entity.AppUser;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {

    AppUserRepository appUserRepository;

    @Autowired
    public AppUserDetailsServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return AppUserDetailsImpl.build(appUser);
    }

}

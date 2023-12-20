package com.balbasio.utility;

import com.balbasio.manager.ElasticSearchUserProfileManager;
import com.balbasio.mapper.UserProfileMapper;
import com.balbasio.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;

//@Component
@RequiredArgsConstructor
public class TestAndRun {

    private final UserProfileRepository repository;
    private final ElasticSearchUserProfileManager manager;
    //@PostConstruct
    public void init(){
       repository.findAll().forEach(u->{
           manager.update(UserProfileMapper.INSTANCE.toUserProfileRequestDto(u));
           System.out.println("gönderildi... "+ u.getUserName());
       });
    }
}

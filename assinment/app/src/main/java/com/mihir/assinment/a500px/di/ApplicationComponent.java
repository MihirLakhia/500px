package com.mihir.assinment.a500px.di;

import android.content.Context;


import com.mihir.assinment.a500px.data.PxPhotoRepository;
import com.mihir.assinment.a500px.service.PxService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {PxServiceModule.class})
public interface ApplicationComponent {
    Context context();

    PxService service();

    PxPhotoRepository repository();
}

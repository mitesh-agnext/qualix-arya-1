package com.custom.app.ui.permission;

import com.base.app.ui.epoxy.BaseEpoxy;

import java.util.List;

public class PermissionController extends BaseEpoxy {

    private List<String> permissions;

    void setList(List<String> permissions) {
        this.permissions = permissions;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        for (String permission : permissions) {
            new PermissionItemModel_()
                    .id(permissions.indexOf(permission))
                    .title(permission)
                    .addTo(this);
        }
    }
}
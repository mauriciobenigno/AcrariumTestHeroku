/*
 * (C) Copyright 2019 Lukas Morawietz (https://github.com/F43nd1r)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.faendir.acra.ui.view.user

import com.faendir.acra.i18n.Messages
import com.faendir.acra.i18n.TranslatableText
import com.faendir.acra.navigation.View
import com.faendir.acra.service.MailService
import com.faendir.acra.service.UserService
import com.faendir.acra.ui.component.HasAcrariumTitle
import com.faendir.acra.ui.component.UserEditor
import com.faendir.acra.ui.ext.content
import com.faendir.acra.ui.view.main.MainView
import com.faendir.acra.util.getCurrentUser
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author lukas
 * @since 26.02.19
 */
@View
@Route(value = "account", layout = MainView::class)
class AccountView constructor(private val userService: UserService, @Autowired(required = false) private val mailService: MailService?) :
    Composite<FlexLayout>(),
    HasAcrariumTitle {
    init {
        content {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER
            val user = userService.getCurrentUser()
            add(UserEditor(userService, mailService, user, true) { Notification.show(getTranslation(Messages.SUCCESS)) })
        }
    }

    override val title = TranslatableText(Messages.ACCOUNT)

}
package com.example.budgetbuddy.viewHolders

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.INVITATION_TYPE
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.ListItemImageLoader
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Clase ViewHolder encargada de vincular los datos de una invitación en el layout cargado en
 * el RecyclerView
 * @param containerView Vista del layout de la categoría
 * @param imageLoader Objeto usado para cargar las fotos de las invitaciones
 * @param context Contexto usado para acceder a los recursos de la aplicación
 * @param onAcceptListener Objeto de clase que implementa la interfaz [InvitationViewHolder.OnClickListener]
 * cuyo método onClick será llamada cada vez que se acepte una invitación
 * @param onDeclineListener Objeto de clase que implementa la interfaz [InvitationViewHolder.OnClickListener]
 * cuyo método onClick será llamada cada vez que se rechace una invitación
 *
 * La manera empleada para el uso del [RecyclerView.ViewHolder] fue obtenida de la fuente:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capitulo 6 'Adding and Interacting with RecyclerView'
 *
 * @author Armando Guzmán
 * */
class InvitationViewHolder(
    private val containerView: View,
    private val imageLoader: ListItemImageLoader,
    private val context: Context,
    private val onAcceptListener: OnClickListener,
    private val onDeclineListener: OnClickListener
) : ListItemViewHolder(containerView) {

    private val imgView: ImageView by lazy {
        containerView.findViewById(R.id.item_img_view)
    }

    private val textView: TextView by lazy {
        containerView.findViewById(R.id.item_text_view)
    }
    private val agoView: TextView by lazy {
        containerView.findViewById(R.id.item_ago_view)
    }
    private val acceptBtn: Button by lazy {
        containerView.findViewById(R.id.acceptBtn)
    }
    private val declineBtn: Button by lazy {
        containerView.findViewById(R.id.declineBtn)
    }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.Invitation) {
            "Expected ListItemUiModel.Invitation $listItem"
        }
        val invitationData = listItem.invitationUiModel

        invitationData.pic?.let { imageLoader.loadImage(it, imgView) }

        acceptBtn.setOnClickListener {
            onAcceptListener.onClick(invitationData, adapterPosition)
        }
        declineBtn.setOnClickListener {
            onDeclineListener.onClick(invitationData, adapterPosition)
        }

        textView.text =
            if (invitationData.type == INVITATION_TYPE.FRIEND_REQUEST) context.getString(
                R.string.friend_invitation_text,
                invitationData.senderName
            ) else context.getString(R.string.group_invitation_text, invitationData.senderName)
        agoView.text = getTimeAgo(getDateSent(invitationData.dateSent))
    }

    /**
     * Método que sirve para obtener hace cuanto tiempo se ha enviado una invitación recibida
     * @param date Fecha usada para determinar cuanto tiempo ha pasado desde que se envió la invitación
     * @return Cuanto tiempo ha pasado en texto
     * */
    private fun getTimeAgo(date: LocalDateTime): String {
        val secondsDifference = ChronoUnit.SECONDS.between(date, LocalDateTime.now())
        val hoursDifference = ChronoUnit.HOURS.between(date, LocalDateTime.now())
        val minutesDifference = ChronoUnit.MINUTES.between(date, LocalDateTime.now())
        val daysDifference = ChronoUnit.DAYS.between(date, LocalDateTime.now())
        val monthsDifference = ChronoUnit.MONTHS.between(date, LocalDateTime.now())

        return if (monthsDifference > 0) {
            context.getString(R.string.months_ago, monthsDifference)
        } else if (daysDifference > 0) {
            context.getString(R.string.days_ago, daysDifference)
        } else if (hoursDifference > 0) {
            context.getString(R.string.hours_ago, hoursDifference)
        } else if (minutesDifference > 0) {
            context.getString(R.string.minutes_ago, minutesDifference)
        } else {
            context.getString(R.string.seconds_ago, secondsDifference)
        }
    }

    interface OnClickListener {
        fun onClick(invitation: InvitationUiModel, position: Int)
    }

    /**
     * Método que sirve para convertir el [Long] obtenido del servidor de Firebase para represntar
     * el momento de envio de la invitación en un objeto de [LocalDateTime]
     * @param dateSent Fecha obtenida del servidor de Firebase
     * @return La fecha convertida en [LocalDateTime]
     * */
    private fun getDateSent(dateSent: Any?): LocalDateTime {
        var dateResult: LocalDateTime = LocalDateTime.now()
        dateSent?.let {
            require(it is Long)
            dateResult = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        return dateResult
    }
}
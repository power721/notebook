package cn.har01d.notebook.entity

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
class Menu(
        @Column(nullable = false) var title: String,
        @Column(nullable = false) var uri: String,
        @Column(nullable = false) var icon: String = "",
        @Column(name = "orders", nullable = false) var order: Int = 0,
        @Column(nullable = false) var parent: Int = 0,
        @Column(nullable = false) var auth: Boolean = false,
        @Column(nullable = false) var admin: Boolean = false,
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
) {
    @Transient
    var children: MutableList<Menu>? = null
}

interface MenuRepository : JpaRepository<Menu, Int>

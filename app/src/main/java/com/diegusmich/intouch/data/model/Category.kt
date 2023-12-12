package com.diegusmich.intouch.data.model

import com.diegusmich.intouch.data.dto.CategoryDTO

data class Category(val id: String, val name: String, val cover: String){
    companion object{
        fun fromDTO(dto: CategoryDTO): Category{
            return Category(
                id = dto.id,
                name = dto.name,
                cover = dto.cover
            )
        }
    }
}
import React from "react";
import { GET } from "../api/images/route";
import { ImgResponse } from "@/dto/imgResponse";
import GalleryImageComponent from "@/components/GalleryImageComponent";

export default async function page() {
  const data = await GET();

  const galleryData: ImgResponse[] = await data.json();

  return (
    <div className="gap-4 mt-16 mx-4 md:mx-8 xl:mx-16 my-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
      {galleryData.map((img) => (
        <GalleryImageComponent
          key={`image_gallery_${img.id}`}
          id={img.id}
        ></GalleryImageComponent>
      ))}
    </div>
  );
}

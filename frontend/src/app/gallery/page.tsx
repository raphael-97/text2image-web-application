import React from "react";
import { GET } from "../api/images/route";
import { ImageResponse } from "@/dto/imageResponse";
import GalleryImageComponent from "@/components/GalleryImageComponent";

export default async function page() {
  const galleryData: ImageResponse[] = await GET();
  return (
    <div className=" gap-7 mx-64 my-8 grid grid-cols-1 sm:grid-cols-5">
      {galleryData.map((img) => (
        <GalleryImageComponent id={img.id}></GalleryImageComponent>
      ))}
    </div>
  );
}

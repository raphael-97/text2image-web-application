import Image from "next/image";
import React from "react";

export default function GalleryImage({ params }: { params: { id: string } }) {
  return (
    <div className="flex justify-center mt-14">
      <Image
        src={`/api/images/${params.id}`}
        alt={`img_${params.id}`}
        width={512}
        height={512}
      ></Image>
    </div>
  );
}

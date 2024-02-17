import { CheckIfUserHasAccessToImage } from "@/app/lib/imageAuthActions";
import Image from "next/image";
import { notFound } from "next/navigation";
import React from "react";

export default async function GalleryImage({
  params,
}: {
  params: { id: string };
}) {
  try {
    await CheckIfUserHasAccessToImage(parseInt(params.id));
  } catch (error) {
    if (error instanceof Error) notFound();
  }

  return (
    <div className="flex justify-center mt-14">
      <Image
        unoptimized
        src={`/api/images/${params.id}`}
        alt={`img_${params.id}`}
        width={512}
        height={512}
      ></Image>
    </div>
  );
}

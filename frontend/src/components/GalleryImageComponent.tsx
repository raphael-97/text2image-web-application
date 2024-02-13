import Image from "next/image";
import Link from "next/link";
import React from "react";

export default function GalleryImageComponent(props: { id: number }) {
  return (
    <div>
      <Link key={props.id} href={`/gallery/${props.id}`} scroll={false}>
        <Image
          src={`/api/images/${props.id}`}
          alt={`img_${props.id}`}
          width={512}
          height={512}
          className="duration-500 hover:scale-105 cursor-pointer rounded-3xl"
        ></Image>
      </Link>
    </div>
  );
}
